package com.anirec.domain.anime.mapper

import com.anirec.domain.anime.dto.AnimeDto
import com.anirec.domain.anime.dto.GenreDto
import com.anirec.domain.anime.entity.Anime
import com.anirec.domain.anime.entity.Genre
import com.anirec.domain.anime.entity.Studio
import java.time.LocalDate

object AnimeDataMapper {

    fun toEntity(
        dto: AnimeDto,
        existingId: Long,
        genreLookup: Map<Long, Genre>,
        studioLookup: Map<Long, Studio>,
    ): Anime {
        val anime = Anime(
            malId = dto.malId,
            title = dto.title,
            titleJapanese = dto.titleJapanese,
            synopsis = dto.synopsis,
            score = dto.score,
            scoredBy = dto.scoredBy,
            rank = dto.rank,
            popularity = dto.popularity,
            members = dto.members,
            episodes = dto.episodes,
            status = dto.status,
            type = dto.type,
            season = dto.season,
            year = dto.year,
            imageUrl = dto.images?.jpg?.imageUrl,
            largeImageUrl = dto.images?.jpg?.largeImageUrl,
            airedFrom = parseDate(dto.aired?.from),
            airedTo = parseDate(dto.aired?.to),
            url = dto.url,
            id = existingId,
        )
        dto.genres?.forEach { malEntity ->
            genreLookup[malEntity.malId]?.let { anime.genres.add(it) }
        }
        dto.studios?.forEach { malEntity ->
            studioLookup[malEntity.malId]?.let { anime.studios.add(it) }
        }
        return anime
    }

    fun toGenreEntity(dto: GenreDto, existingId: Long): Genre = Genre(
        malId = dto.malId,
        name = dto.name,
        count = dto.count,
        id = existingId,
    )

    fun parseDate(dateStr: String?): LocalDate? {
        if (dateStr == null) return null
        return try {
            LocalDate.parse(dateStr.take(10))
        } catch (_: Exception) {
            null
        }
    }
}
