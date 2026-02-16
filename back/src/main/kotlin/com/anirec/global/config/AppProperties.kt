package com.anirec.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val supabase: Supabase,
    val cors: Cors,
) {
    data class Supabase(val url: String)
    data class Cors(val allowedOrigins: String)
}
