package com.anirec.global.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
@EnableWebFluxSecurity
@Profile("!test")
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        converter: JwtAuthenticationConverter,
        manager: JwtAuthenticationManager,
    ): SecurityWebFilterChain {
        val authFilter = AuthenticationWebFilter(manager).apply {
            setServerAuthenticationConverter(converter)
        }

        return http
            .cors {}
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange {
                it.pathMatchers("/actuator/health").permitAll()
                it.pathMatchers(HttpMethod.GET, "/api/anime", "/api/anime/**").permitAll()
                it.pathMatchers("/api/admin/**").authenticated()
                it.pathMatchers("/api/**").authenticated()
                it.anyExchange().permitAll()
            }
            .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}
