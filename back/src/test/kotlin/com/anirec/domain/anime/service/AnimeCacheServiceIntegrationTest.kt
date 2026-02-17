package com.anirec.domain.anime.service

import com.anirec.domain.anime.client.JikanClient
import com.anirec.domain.anime.dto.AnimeDto
import com.anirec.domain.anime.dto.JikanResponse
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Testcontainers
class AnimeCacheServiceIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val redis: GenericContainer<*> = GenericContainer("redis:7-alpine")
            .withExposedPorts(6379)

        private lateinit var connectionFactory: LettuceConnectionFactory
        private lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

        @BeforeAll
        @JvmStatic
        fun setUpRedis() {
            connectionFactory = LettuceConnectionFactory(redis.host, redis.getMappedPort(6379))
            connectionFactory.afterPropertiesSet()

            val serializer = StringRedisSerializer()
            val context = RedisSerializationContext.newSerializationContext<String, String>(serializer)
                .value(serializer)
                .build()
            redisTemplate = ReactiveRedisTemplate(connectionFactory, context)
        }

        @AfterAll
        @JvmStatic
        fun tearDownRedis() {
            connectionFactory.destroy()
        }
    }

    private val jikanClient: JikanClient = mockk()
    private val objectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    private lateinit var cacheService: AnimeCacheService

    private val sampleAnime = AnimeDto(
        malId = 1,
        title = "Cowboy Bebop",
        titleJapanese = "カウボーイビバップ",
        synopsis = "A space bounty hunter crew.",
        score = 8.78,
        scoredBy = 900000,
        rank = 28,
        popularity = 39,
        members = 1800000,
        episodes = 26,
        status = "Finished Airing",
        type = "TV",
        season = "spring",
        year = 1998,
        genres = listOf(AnimeDto.MalEntity(1, "Action")),
        studios = listOf(AnimeDto.MalEntity(14, "Sunrise")),
        images = AnimeDto.Images(AnimeDto.Images.JpgImage("url", "large_url")),
        aired = AnimeDto.Aired("1998-04-03", "1999-04-24"),
        url = "https://myanimelist.net/anime/1/Cowboy_Bebop",
    )

    private val sampleResponse = JikanResponse(
        pagination = JikanResponse.Pagination(
            lastVisiblePage = 1,
            hasNextPage = false,
            currentPage = 1,
            items = JikanResponse.PaginationItems(count = 1, total = 1, perPage = 25),
        ),
        data = listOf(sampleAnime),
    )

    @BeforeEach
    fun setUp() {
        cacheService = AnimeCacheService(jikanClient, redisTemplate, objectMapper)
        // Flush all keys before each test
        redisTemplate.connectionFactory.reactiveConnection.serverCommands().flushAll().block()
    }

    @Test
    fun `searchAnime caches response and returns from cache on second call`() = runTest {
        coEvery { jikanClient.searchAnime(query = "bebop", page = 1) } returns sampleResponse

        // First call: should hit the API
        val first = cacheService.searchAnime(query = "bebop", page = 1)
        assertEquals(1, first.data.size)
        assertEquals("Cowboy Bebop", first.data[0].title)

        // Second call: should return from cache (API not called again)
        val second = cacheService.searchAnime(query = "bebop", page = 1)
        assertEquals(1, second.data.size)
        assertEquals("Cowboy Bebop", second.data[0].title)

        coVerify(exactly = 1) { jikanClient.searchAnime(query = "bebop", page = 1) }
    }

    @Test
    fun `different params produce different cache keys`() = runTest {
        val response2 = sampleResponse.copy(
            data = listOf(sampleAnime.copy(malId = 2, title = "Another Anime")),
        )
        coEvery { jikanClient.searchAnime(query = "bebop", page = 1) } returns sampleResponse
        coEvery { jikanClient.searchAnime(query = "bebop", page = 2) } returns response2

        val first = cacheService.searchAnime(query = "bebop", page = 1)
        val second = cacheService.searchAnime(query = "bebop", page = 2)

        assertEquals("Cowboy Bebop", first.data[0].title)
        assertEquals("Another Anime", second.data[0].title)

        coVerify(exactly = 1) { jikanClient.searchAnime(query = "bebop", page = 1) }
        coVerify(exactly = 1) { jikanClient.searchAnime(query = "bebop", page = 2) }
    }

    @Test
    fun `getTopAnime caches response`() = runTest {
        coEvery { jikanClient.getTopAnime(page = 1, limit = 25) } returns sampleResponse

        cacheService.getTopAnime(page = 1, limit = 25)
        cacheService.getTopAnime(page = 1, limit = 25)

        coVerify(exactly = 1) { jikanClient.getTopAnime(page = 1, limit = 25) }
    }

    @Test
    fun `getSeasonalAnime caches response`() = runTest {
        coEvery { jikanClient.getSeasonalAnime(2024, "winter", page = 1) } returns sampleResponse

        val result = cacheService.getSeasonalAnime(2024, "winter", page = 1)
        assertNotNull(result.pagination)
        assertEquals(1, result.data.size)

        cacheService.getSeasonalAnime(2024, "winter", page = 1)

        coVerify(exactly = 1) { jikanClient.getSeasonalAnime(2024, "winter", page = 1) }
    }

    @Test
    fun `getCurrentSeasonAnime caches response`() = runTest {
        coEvery { jikanClient.getCurrentSeasonAnime(page = 1) } returns sampleResponse

        cacheService.getCurrentSeasonAnime(page = 1)
        cacheService.getCurrentSeasonAnime(page = 1)

        coVerify(exactly = 1) { jikanClient.getCurrentSeasonAnime(page = 1) }
    }
}
