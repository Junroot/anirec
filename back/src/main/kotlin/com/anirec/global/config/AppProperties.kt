package com.anirec.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val supabase: Supabase,
    val cors: Cors,
    val jikan: Jikan,
    val sync: Sync,
) {
    data class Supabase(val url: String)
    data class Cors(val allowedOrigins: String)
    data class Jikan(val baseUrl: String)
    data class Sync(
        val enabled: Boolean,
        val seasonCron: String,
        val fullCron: String,
    )
}
