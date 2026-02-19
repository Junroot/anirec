package com.anirec.domain.rating.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RatingRequest(
    @JsonProperty("anime_id") val animeId: Long,
    val score: Int,
    @JsonProperty("watch_status") val watchStatus: String,
)
