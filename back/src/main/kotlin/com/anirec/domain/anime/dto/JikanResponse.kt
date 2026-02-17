package com.anirec.domain.anime.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JikanResponse(
    val pagination: Pagination?,
    val data: List<AnimeDto>,
) {
    data class Pagination(
        @JsonProperty("last_visible_page") val lastVisiblePage: Int,
        @JsonProperty("has_next_page") val hasNextPage: Boolean,
        @JsonProperty("current_page") val currentPage: Int,
        val items: PaginationItems?,
    )

    data class PaginationItems(
        val count: Int,
        val total: Int,
        @JsonProperty("per_page") val perPage: Int,
    )
}
