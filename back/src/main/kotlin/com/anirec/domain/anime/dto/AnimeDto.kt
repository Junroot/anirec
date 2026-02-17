package com.anirec.domain.anime.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AnimeDto(
    @JsonProperty("mal_id") val malId: Long,
    val title: String,
    @JsonProperty("title_japanese") val titleJapanese: String?,
    val synopsis: String?,
    val score: Double?,
    @JsonProperty("scored_by") val scoredBy: Long?,
    val rank: Int?,
    val popularity: Int?,
    val members: Long?,
    val episodes: Int?,
    val status: String?,
    val type: String?,
    val season: String?,
    val year: Int?,
    val genres: List<MalEntity>?,
    val studios: List<MalEntity>?,
    val images: Images?,
    val aired: Aired?,
    val url: String?,
) {
    data class Images(
        val jpg: JpgImage?,
    ) {
        data class JpgImage(
            @JsonProperty("image_url") val imageUrl: String?,
            @JsonProperty("large_image_url") val largeImageUrl: String?,
        )
    }

    data class Aired(
        val from: String?,
        val to: String?,
    )

    data class MalEntity(
        @JsonProperty("mal_id") val malId: Long,
        val name: String,
    )
}
