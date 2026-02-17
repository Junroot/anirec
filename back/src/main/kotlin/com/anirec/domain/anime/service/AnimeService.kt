package com.anirec.domain.anime.service

import com.anirec.domain.anime.client.JikanClient
import com.anirec.domain.anime.dto.JikanResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AnimeService(
    private val jikanClient: JikanClient,
    @Autowired(required = false) private val animeCacheService: AnimeCacheService?,
) {

    suspend fun search(
        query: String? = null,
        page: Int? = null,
        limit: Int? = null,
        type: String? = null,
        genres: String? = null,
        orderBy: String? = null,
        sort: String? = null,
    ): JikanResponse =
        animeCacheService?.searchAnime(query, page, limit, type, orderBy = orderBy, sort = sort, genres = genres)
            ?: jikanClient.searchAnime(query, page, limit, type, orderBy = orderBy, sort = sort, genres = genres)

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
}
