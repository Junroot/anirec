package com.anirec.domain.anime.service

import com.anirec.domain.anime.client.JikanClient
import com.anirec.domain.anime.dto.JikanGenreResponse
import com.anirec.domain.anime.dto.JikanProducerResponse
import com.anirec.domain.anime.dto.JikanResponse
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
@ConditionalOnBean(ReactiveRedisConnectionFactory::class)
class AnimeCacheService(
    private val jikanClient: JikanClient,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private val SEARCH_TTL = Duration.ofHours(1)
        private val TOP_SEASON_TTL = Duration.ofHours(6)
        private const val KEY_PREFIX = "anime"
    }

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
    ): JikanResponse {
        val key = buildKey(
            "search",
            "q" to query, "page" to page, "limit" to limit,
            "type" to type, "status" to status, "orderBy" to orderBy, "sort" to sort,
            "genres" to genres, "producers" to producers,
        )
        return getOrFetch(key, SEARCH_TTL, JikanResponse::class.java) {
            jikanClient.searchAnime(query, page, limit, type, status, orderBy, sort, genres, producers)
        }
    }

    suspend fun getTopAnime(
        page: Int? = null,
        limit: Int? = null,
        filter: String? = null,
    ): JikanResponse {
        val key = buildKey("top", "page" to page, "limit" to limit, "filter" to filter)
        return getOrFetch(key, TOP_SEASON_TTL, JikanResponse::class.java) {
            jikanClient.getTopAnime(page, limit, filter)
        }
    }

    suspend fun getSeasonalAnime(
        year: Int,
        season: String,
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse {
        val key = buildKey("seasonal", "year" to year, "season" to season, "page" to page, "limit" to limit)
        return getOrFetch(key, TOP_SEASON_TTL, JikanResponse::class.java) {
            jikanClient.getSeasonalAnime(year, season, page, limit)
        }
    }

    suspend fun getCurrentSeasonAnime(
        page: Int? = null,
        limit: Int? = null,
    ): JikanResponse {
        val key = buildKey("current-season", "page" to page, "limit" to limit)
        return getOrFetch(key, TOP_SEASON_TTL, JikanResponse::class.java) {
            jikanClient.getCurrentSeasonAnime(page, limit)
        }
    }

    suspend fun getAnimeGenres(): JikanGenreResponse {
        val key = buildKey("genres")
        return getOrFetch(key, TOP_SEASON_TTL, JikanGenreResponse::class.java) {
            jikanClient.getAnimeGenres()
        }
    }

    suspend fun searchProducers(
        q: String? = null,
        page: Int? = null,
        limit: Int? = null,
    ): JikanProducerResponse {
        val key = buildKey("producers", "q" to q, "page" to page, "limit" to limit)
        return getOrFetch(key, SEARCH_TTL, JikanProducerResponse::class.java) {
            jikanClient.searchProducers(q, page, limit)
        }
    }

    private suspend fun <T : Any> getOrFetch(
        key: String,
        ttl: Duration,
        clazz: Class<T>,
        fetcher: suspend () -> T,
    ): T {
        val cached = get(key, clazz)
        if (cached != null) return cached

        val response = fetcher()
        put(key, response, ttl)
        return response
    }

    private suspend fun <T : Any> get(key: String, clazz: Class<T>): T? =
        try {
            val json = redisTemplate.opsForValue().get(key).awaitSingleOrNull()
            json?.let { objectMapper.readValue(it, clazz) }
        } catch (e: Exception) {
            log.warn("Redis GET failed for key={}: {}", key, e.message)
            null
        }

    private suspend fun put(key: String, response: Any, ttl: Duration) {
        try {
            val json = objectMapper.writeValueAsString(response)
            redisTemplate.opsForValue().set(key, json, ttl).awaitSingle()
        } catch (e: Exception) {
            log.warn("Redis SET failed for key={}: {}", key, e.message)
        }
    }

    private fun buildKey(operation: String, vararg params: Pair<String, Any?>): String {
        val paramPart = params
            .filter { it.second != null }
            .joinToString(":") { "${it.first}=${it.second}" }
        return if (paramPart.isEmpty()) "$KEY_PREFIX:$operation" else "$KEY_PREFIX:$operation:$paramPart"
    }
}
