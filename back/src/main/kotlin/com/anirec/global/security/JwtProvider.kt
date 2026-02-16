package com.anirec.global.security

import com.anirec.global.config.AppProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Locator
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URI
import java.security.AlgorithmParameters
import java.security.Key
import java.security.KeyFactory
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference

@Component
class JwtProvider(private val props: AppProperties) {

    private val objectMapper = ObjectMapper()
    private val cachedKeys = AtomicReference<Map<String, Key>>(emptyMap())

    private val jwksUrl: String
        get() = "${props.supabase.url}/auth/v1/.well-known/jwks.json"

    private val keyLocator = Locator { header ->
		val kid = header["kid"] as? String
			?: throw SecurityException("No kid in JWT header")
		resolveKey(kid)
	}

    fun parseToken(token: String): Claims =
        Jwts.parser()
            .keyLocator(keyLocator)
            .build()
            .parseSignedClaims(token)
            .payload

    fun getUserId(claims: Claims): String =
        claims.subject

    fun getEmail(claims: Claims): String =
        claims["email"] as? String ?: ""

    @Suppress("UNCHECKED_CAST")
    fun getUsername(claims: Claims): String {
        val metadata = claims["user_metadata"] as? Map<String, Any>
        val username = metadata?.get("username") as? String
        if (!username.isNullOrBlank()) return username

        val email = getEmail(claims)
        return if (email.contains("@")) email.substringBefore("@") else email
    }

    private fun resolveKey(kid: String): Key {
        cachedKeys.get()[kid]?.let { return it }

        // Key not found in cache, refresh from JWKS endpoint
        val refreshed = fetchAndCacheKeys()
        return refreshed[kid]
            ?: throw SecurityException("Unknown key ID: $kid")
    }

    private fun fetchAndCacheKeys(): Map<String, Key> {
        val json = fetchJwksJson()
        val jwks = objectMapper.readTree(json)
        val keys = mutableMapOf<String, Key>()

        for (jwk in jwks["keys"]) {
            val kid = jwk["kid"]?.asText() ?: continue
            val kty = jwk["kty"]?.asText() ?: continue

            if (kty == "EC") {
                keys[kid] = parseEcPublicKey(jwk)
            }
        }

        cachedKeys.set(keys)
        return keys
    }

    private fun fetchJwksJson(): String {
        val connection = URI(jwksUrl).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 5_000
        connection.readTimeout = 5_000
        return connection.inputStream.bufferedReader().readText()
    }

    private fun parseEcPublicKey(jwk: JsonNode): ECPublicKey {
        val crv = jwk["crv"].asText()
        val x = Base64.getUrlDecoder().decode(jwk["x"].asText())
        val y = Base64.getUrlDecoder().decode(jwk["y"].asText())

        val curveName = when (crv) {
            "P-256" -> "secp256r1"
            "P-384" -> "secp384r1"
            "P-521" -> "secp521r1"
            else -> throw IllegalArgumentException("Unsupported curve: $crv")
        }

        val ecParams = AlgorithmParameters.getInstance("EC").apply {
            init(ECGenParameterSpec(curveName))
        }.getParameterSpec(ECParameterSpec::class.java)

        val point = ECPoint(BigInteger(1, x), BigInteger(1, y))
        val pubKeySpec = ECPublicKeySpec(point, ecParams)
        return KeyFactory.getInstance("EC").generatePublic(pubKeySpec) as ECPublicKey
    }
}
