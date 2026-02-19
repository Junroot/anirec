package com.anirec.domain.rating.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class RatingWithAnimeResponse(
    val id: Long,
    @JsonProperty("anime_id") val animeId: Long,
    @JsonProperty("anime_title") val animeTitle: String,
    @JsonProperty("anime_image_url") val animeImageUrl: String?,
    @JsonProperty("anime_type") val animeType: String?,
    @JsonProperty("anime_episodes") val animeEpisodes: Int?,
    val score: Int,
    @JsonProperty("watch_status") val watchStatus: String,
    @JsonProperty("created_at") val createdAt: LocalDateTime,
    @JsonProperty("updated_at") val updatedAt: LocalDateTime,
)
