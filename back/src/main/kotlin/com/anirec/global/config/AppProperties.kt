package com.anirec.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val supabase: Supabase,
    val cors: Cors,
    val jikan: Jikan,
) {
    data class Supabase(val url: String)
    data class Cors(val allowedOrigins: String)
    data class Jikan(val baseUrl: String)
}
