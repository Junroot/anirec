package com.anirec.domain.rating.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class RatingResponse(
    val id: Long,
    @JsonProperty("anime_id") val animeId: Long,
    val score: Int,
    @JsonProperty("watch_status") val watchStatus: String,
    @JsonProperty("created_at") val createdAt: LocalDateTime,
    @JsonProperty("updated_at") val updatedAt: LocalDateTime,
)
