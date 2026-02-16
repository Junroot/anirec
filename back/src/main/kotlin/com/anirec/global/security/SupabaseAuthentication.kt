package com.anirec.global.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class SupabaseAuthentication(
    val userId: String,
    val email: String,
    val username: String,
) : AbstractAuthenticationToken(emptyList()) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any? = null
    override fun getPrincipal(): String = userId
}
