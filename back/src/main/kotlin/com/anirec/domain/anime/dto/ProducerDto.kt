package com.anirec.domain.anime.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JikanProducerResponse(
    val pagination: JikanResponse.Pagination?,
    val data: List<ProducerDto>,
)

data class ProducerDto(
    @JsonProperty("mal_id") val malId: Long,
    val titles: List<ProducerTitle>?,
    val count: Int?,
) {
    data class ProducerTitle(
        val type: String,
        val title: String,
    )
}

data class ProducerSimple(
    val id: Long,
    val name: String,
)
