package com.anirec.domain.anime.dto

import com.anirec.domain.anime.entity.Anime

object AnimeMapper {

    fun toDto(anime: Anime): AnimeDto = AnimeDto(
        malId = anime.malId,
        title = anime.title,
        titleJapanese = anime.titleJapanese,
        synopsis = anime.synopsis,
        score = anime.score,
        scoredBy = anime.scoredBy,
        rank = anime.rank,
        popularity = anime.popularity,
        members = anime.members,
        episodes = anime.episodes,
        status = anime.status,
        type = anime.type,
        season = anime.season,
        year = anime.year,
        genres = anime.genres.map { AnimeDto.MalEntity(malId = it.malId, name = it.name) },
        studios = anime.studios.map { AnimeDto.MalEntity(malId = it.malId, name = it.name) },
        images = AnimeDto.Images(
            jpg = AnimeDto.Images.JpgImage(
                imageUrl = anime.imageUrl,
                largeImageUrl = anime.largeImageUrl,
            ),
        ),
        aired = AnimeDto.Aired(
            from = anime.airedFrom?.toString(),
            to = anime.airedTo?.toString(),
        ),
        url = anime.url,
    )
}
