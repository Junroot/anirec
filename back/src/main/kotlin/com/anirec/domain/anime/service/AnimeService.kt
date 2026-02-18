package com.anirec.domain.anime.service

import com.anirec.domain.anime.dto.GenreSimple
import com.anirec.domain.anime.dto.JikanResponse
import com.anirec.domain.anime.dto.PaginationUtils
import com.anirec.domain.anime.dto.ProducerSimple
import com.anirec.domain.anime.repository.AnimeRepository
import com.anirec.domain.anime.repository.GenreRepository
import com.anirec.domain.anime.repository.StudioRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AnimeService(
    private val animeRepository: AnimeRepository,
    private val genreRepository: GenreRepository,
    private val studioRepository: StudioRepository,
) {

    fun search(
        page: Int? = null,
        limit: Int? = null,
        type: String? = null,
        status: String? = null,
        genres: String? = null,
        orderBy: String? = null,
        sort: String? = null,
        producers: String? = null,
    ): JikanResponse {
        val genreMalIds = genres?.split(",")?.mapNotNull { it.trim().toLongOrNull() }
        val producerMalIds = producers?.split(",")?.mapNotNull { it.trim().toLongOrNull() }
        val pageable = PaginationUtils.toPageable(page, limit)

        val result = animeRepository.search(
            type = type,
            status = status,
            genreMalIds = genreMalIds,
            producerMalIds = producerMalIds,
            orderBy = orderBy,
            sort = sort,
            pageable = pageable,
        )
        return PaginationUtils.toJikanResponse(result)
    }

    fun getTop(
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse {
        val pageable = PaginationUtils.toPageable(page, limit)
        val result = animeRepository.findTop(pageable)
        return PaginationUtils.toJikanResponse(result)
    }

    fun getSeasonal(
        year: Int,
        season: String,
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse {
        val pageable = PaginationUtils.toPageable(page, limit)
        val result = animeRepository.findBySeason(year, season, pageable)
        return PaginationUtils.toJikanResponse(result)
    }

    fun getCurrentSeason(
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse {
        val now = LocalDate.now()
        val year = now.year
        val season = when (now.monthValue) {
            in 1..3 -> "winter"
            in 4..6 -> "spring"
            in 7..9 -> "summer"
            else -> "fall"
        }
        return getSeasonal(year = year, season = season, page = page, limit = limit)
    }

    fun searchGenres(
        q: String? = null,
    ): List<GenreSimple> {
        val genres = if (q.isNullOrBlank()) {
            genreRepository.findAllByOrderByCountDesc()
        } else {
            genreRepository.findByNameContainingIgnoreCaseOrderByCountDesc(q)
        }
        return genres.map { GenreSimple(id = it.malId, name = it.name) }
    }

    fun searchProducers(
        q: String? = null,
        page: Int? = null,
        limit: Int? = null,
    ): List<ProducerSimple> {
        if (q.isNullOrBlank()) return emptyList()
        val studios = studioRepository.findByNameContainingIgnoreCase(q)
        val effectiveLimit = limit ?: 10
        return studios.take(effectiveLimit).map { ProducerSimple(id = it.malId, name = it.name) }
    }
}
