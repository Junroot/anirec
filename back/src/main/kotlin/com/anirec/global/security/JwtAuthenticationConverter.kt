package com.anirec.global.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationConverter : ServerAuthenticationConverter {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?: return Mono.empty()

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            return Mono.empty()
        }

        val token = authHeader.substring(BEARER_PREFIX.length)
        return Mono.just(UsernamePasswordAuthenticationToken(token, token))
    }
}
