package com.anirec.domain.anime.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JikanGenreResponse(
    val data: List<GenreDto>,
)

data class GenreDto(
    @JsonProperty("mal_id") val malId: Long,
    val name: String,
    val count: Int?,
)

data class GenreSimple(
    val id: Long,
    val name: String,
)
