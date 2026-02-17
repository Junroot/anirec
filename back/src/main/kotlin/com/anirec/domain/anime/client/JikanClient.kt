package com.anirec.domain.anime.client

import com.anirec.domain.anime.dto.JikanGenreResponse
import com.anirec.domain.anime.dto.JikanProducerResponse
import com.anirec.domain.anime.dto.JikanResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

@Component
class JikanClient(private val jikanWebClient: WebClient) {

    suspend fun searchAnime(
        query: String? = null,
        page: Int? = null,
        limit: Int? = null,
        type: String? = null,
        status: String? = null,
        orderBy: String? = null,
        sort: String? = null,
        genres: String? = null,
        producers: String? = null,
    ): JikanResponse =
        jikanWebClient.get()
            .uri { builder ->
                builder.path("/anime")
                    .queryParamIfPresent("q", query)
                    .queryParamIfPresent("page", page)
                    .queryParamIfPresent("limit", limit)
                    .queryParamIfPresent("type", type)
                    .queryParamIfPresent("status", status)
                    .queryParamIfPresent("order_by", orderBy)
                    .queryParamIfPresent("sort", sort)
                    .queryParamIfPresent("genres", genres)
                    .queryParamIfPresent("producers", producers)
					.queryParam("sfw", true)
                    .build()
            }
            .retrieve()
            .bodyToMono(JikanResponse::class.java)
            .awaitSingle()

    suspend fun getTopAnime(
        page: Int? = null,
        limit: Int? = null,
        filter: String? = null,
    ): JikanResponse =
        jikanWebClient.get()
            .uri { builder ->
                builder.path("/top/anime")
                    .queryParamIfPresent("page", page)
                    .queryParamIfPresent("limit", limit)
                    .queryParamIfPresent("filter", filter)
					.queryParam("sfw", true)
                    .build()
            }
            .retrieve()
            .bodyToMono(JikanResponse::class.java)
            .awaitSingle()

    suspend fun getSeasonalAnime(
        year: Int,
        season: String,
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse =
        jikanWebClient.get()
            .uri { builder ->
                builder.path("/seasons/{year}/{season}")
                    .queryParamIfPresent("page", page)
                    .queryParamIfPresent("limit", limit)
					.queryParam("sfw", true)
                    .build(year, season)
            }
            .retrieve()
            .bodyToMono(JikanResponse::class.java)
            .awaitSingle()

    suspend fun getCurrentSeasonAnime(
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse =
        jikanWebClient.get()
            .uri { builder ->
                builder.path("/seasons/now")
                    .queryParamIfPresent("page", page)
                    .queryParamIfPresent("limit", limit)
                    .build()
            }
            .retrieve()
            .bodyToMono(JikanResponse::class.java)
            .awaitSingle()

    suspend fun getAnimeGenres(): JikanGenreResponse =
        jikanWebClient.get()
            .uri { builder ->
                builder.path("/genres/anime")
                    .build()
            }
            .retrieve()
            .bodyToMono(JikanGenreResponse::class.java)
            .awaitSingle()

    suspend fun searchProducers(
        q: String? = null,
        page: Int? = null,
        limit: Int? = null,
    ): JikanProducerResponse =
        jikanWebClient.get()
            .uri { builder ->
                builder.path("/producers")
                    .queryParamIfPresent("q", q)
                    .queryParamIfPresent("page", page)
                    .queryParamIfPresent("limit", limit)
                    .queryParam("order_by", "count")
                    .queryParam("sort", "desc")
                    .build()
            }
            .retrieve()
            .bodyToMono(JikanProducerResponse::class.java)
            .awaitSingle()

    private fun UriBuilder.queryParamIfPresent(name: String, value: Any?): UriBuilder =
        if (value != null) queryParam(name, value) else this
}
