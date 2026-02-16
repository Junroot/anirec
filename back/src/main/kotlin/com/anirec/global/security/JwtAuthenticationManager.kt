package com.anirec.global.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class JwtAuthenticationManager(
    private val jwtProvider: JwtProvider,
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> =
        Mono.fromCallable {
            val token = authentication.credentials as String
            val claims = jwtProvider.parseToken(token)

            SupabaseAuthentication(
                userId = jwtProvider.getUserId(claims),
                email = jwtProvider.getEmail(claims),
                username = jwtProvider.getUsername(claims),
            )
        }
            .subscribeOn(Schedulers.boundedElastic())
            .cast(Authentication::class.java)
}
