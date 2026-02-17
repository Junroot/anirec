package com.anirec.domain.anime.service

import com.anirec.domain.anime.client.JikanClient
import com.anirec.domain.anime.dto.GenreSimple
import com.anirec.domain.anime.dto.JikanResponse
import com.anirec.domain.anime.dto.ProducerSimple
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AnimeService(
    private val jikanClient: JikanClient,
    @Autowired(required = false) private val animeCacheService: AnimeCacheService?,
) {

    suspend fun search(
        page: Int? = null,
        limit: Int? = null,
        type: String? = null,
        status: String? = null,
        genres: String? = null,
        orderBy: String? = null,
        sort: String? = null,
        producers: String? = null,
    ): JikanResponse =
        animeCacheService?.searchAnime(page = page, limit = limit, type = type, status = status, orderBy = orderBy, sort = sort, genres = genres, producers = producers)
            ?: jikanClient.searchAnime(page = page, limit = limit, type = type, status = status, orderBy = orderBy, sort = sort, genres = genres, producers = producers)

    suspend fun getTop(
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse =
        animeCacheService?.getTopAnime(page, limit)
            ?: jikanClient.getTopAnime(page, limit)

    suspend fun getSeasonal(
        year: Int,
        season: String,
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse =
        animeCacheService?.getSeasonalAnime(year, season, page, limit)
            ?: jikanClient.getSeasonalAnime(year, season, page, limit)

    suspend fun getCurrentSeason(
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse =
        animeCacheService?.getCurrentSeasonAnime(page, limit)
            ?: jikanClient.getCurrentSeasonAnime(page, limit)

    suspend fun searchGenres(
        q: String? = null,
    ): List<GenreSimple> {
        val response = animeCacheService?.getAnimeGenres()
            ?: jikanClient.getAnimeGenres()
        return response.data
            .filter { q == null || it.name.contains(q, ignoreCase = true) }
            .map { GenreSimple(id = it.malId, name = it.name) }
    }

    suspend fun searchProducers(
        q: String? = null,
        page: Int? = null,
        limit: Int? = null,
    ): List<ProducerSimple> {
        val response = animeCacheService?.searchProducers(q = q, page = page, limit = limit)
            ?: jikanClient.searchProducers(q = q, page = page, limit = limit)
        return response.data
            .map { dto ->
                val name = dto.titles
                    ?.firstOrNull { it.type == "Default" }
                    ?.title
                    ?: dto.titles?.firstOrNull()?.title
                    ?: "Unknown"
                ProducerSimple(id = dto.malId, name = name)
            }
    }
}
