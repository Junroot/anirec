package com.anirec.domain.anime.service

import com.anirec.domain.anime.client.JikanClient
import com.anirec.domain.anime.dto.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AnimeServiceTest {

    private val jikanClient: JikanClient = mockk()
    private val animeCacheService: AnimeCacheService = mockk()

    private val sampleResponse = JikanResponse(
        pagination = JikanResponse.Pagination(
            lastVisiblePage = 1,
            hasNextPage = false,
            currentPage = 1,
            items = JikanResponse.PaginationItems(count = 1, total = 1, perPage = 25),
        ),
        data = listOf(
            AnimeDto(
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
        ),
    )

    private val sampleGenreResponse = JikanGenreResponse(
        data = listOf(
            GenreDto(malId = 1, name = "Action", count = 5439),
            GenreDto(malId = 4, name = "Comedy", count = 7000),
            GenreDto(malId = 8, name = "Drama", count = 3000),
        ),
    )

    private val sampleProducerResponse = JikanProducerResponse(
        pagination = JikanResponse.Pagination(
            lastVisiblePage = 1,
            hasNextPage = false,
            currentPage = 1,
            items = JikanResponse.PaginationItems(count = 1, total = 1, perPage = 10),
        ),
        data = listOf(
            ProducerDto(
                malId = 569,
                titles = listOf(
                    ProducerDto.ProducerTitle("Default", "MAPPA"),
                    ProducerDto.ProducerTitle("Japanese", "MAPPA"),
                ),
                count = 150,
            ),
        ),
    )

    @Nested
    inner class WithCacheService {
        private val service = AnimeService(jikanClient, animeCacheService)

        @Test
        fun `search delegates to AnimeCacheService`() = runTest {
            coEvery { animeCacheService.searchAnime(status = "airing", page = 1) } returns sampleResponse

            val result = service.search(status = "airing", page = 1)

            assertEquals(1, result.data.size)
            assertEquals("Cowboy Bebop", result.data[0].title)
            coVerify(exactly = 1) { animeCacheService.searchAnime(status = "airing", page = 1) }
            coVerify(exactly = 0) { jikanClient.searchAnime(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }

        @Test
        fun `getTop delegates to AnimeCacheService`() = runTest {
            coEvery { animeCacheService.getTopAnime(page = 1, limit = 25) } returns sampleResponse

            val result = service.getTop(page = 1, limit = 25)

            assertEquals(1, result.data.size)
            coVerify(exactly = 1) { animeCacheService.getTopAnime(page = 1, limit = 25) }
            coVerify(exactly = 0) { jikanClient.getTopAnime(any(), any(), any()) }
        }

        @Test
        fun `getSeasonal delegates to AnimeCacheService`() = runTest {
            coEvery { animeCacheService.getSeasonalAnime(2024, "winter", page = 1) } returns sampleResponse

            val result = service.getSeasonal(year = 2024, season = "winter", page = 1)

            assertEquals(1, result.data.size)
            coVerify(exactly = 1) { animeCacheService.getSeasonalAnime(2024, "winter", page = 1) }
            coVerify(exactly = 0) { jikanClient.getSeasonalAnime(any(), any(), any(), any()) }
        }

        @Test
        fun `getCurrentSeason delegates to AnimeCacheService`() = runTest {
            coEvery { animeCacheService.getCurrentSeasonAnime(page = 1) } returns sampleResponse

            val result = service.getCurrentSeason(page = 1)

            assertEquals(1, result.data.size)
            coVerify(exactly = 1) { animeCacheService.getCurrentSeasonAnime(page = 1) }
            coVerify(exactly = 0) { jikanClient.getCurrentSeasonAnime(any(), any()) }
        }

        @Test
        fun `searchGenres delegates to AnimeCacheService and filters by query`() = runTest {
            coEvery { animeCacheService.getAnimeGenres() } returns sampleGenreResponse

            val result = service.searchGenres(q = "act")

            assertEquals(1, result.size)
            assertEquals(1L, result[0].id)
            assertEquals("Action", result[0].name)
            coVerify(exactly = 1) { animeCacheService.getAnimeGenres() }
            coVerify(exactly = 0) { jikanClient.getAnimeGenres() }
        }

        @Test
        fun `searchGenres returns all when query is null`() = runTest {
            coEvery { animeCacheService.getAnimeGenres() } returns sampleGenreResponse

            val result = service.searchGenres(q = null)

            assertEquals(3, result.size)
        }

        @Test
        fun `searchProducers delegates to AnimeCacheService`() = runTest {
            coEvery { animeCacheService.searchProducers(q = "mappa", page = 1, limit = 10) } returns sampleProducerResponse

            val result = service.searchProducers(q = "mappa", page = 1, limit = 10)

            assertEquals(1, result.size)
            assertEquals(569L, result[0].id)
            assertEquals("MAPPA", result[0].name)
            coVerify(exactly = 1) { animeCacheService.searchProducers(q = "mappa", page = 1, limit = 10) }
            coVerify(exactly = 0) { jikanClient.searchProducers(any(), any(), any()) }
        }
    }

    @Nested
    inner class WithoutCacheService {
        private val service = AnimeService(jikanClient, null)

        @Test
        fun `search falls back to JikanClient`() = runTest {
            coEvery { jikanClient.searchAnime(status = "airing", page = 1) } returns sampleResponse

            val result = service.search(status = "airing", page = 1)

            assertEquals(1, result.data.size)
            assertEquals("Cowboy Bebop", result.data[0].title)
            coVerify(exactly = 1) { jikanClient.searchAnime(status = "airing", page = 1) }
        }

        @Test
        fun `getTop falls back to JikanClient`() = runTest {
            coEvery { jikanClient.getTopAnime(page = 1, limit = 25) } returns sampleResponse

            val result = service.getTop(page = 1, limit = 25)

            assertEquals(1, result.data.size)
            coVerify(exactly = 1) { jikanClient.getTopAnime(page = 1, limit = 25) }
        }

        @Test
        fun `getSeasonal falls back to JikanClient`() = runTest {
            coEvery { jikanClient.getSeasonalAnime(2024, "winter", page = 1) } returns sampleResponse

            val result = service.getSeasonal(year = 2024, season = "winter", page = 1)

            assertEquals(1, result.data.size)
            coVerify(exactly = 1) { jikanClient.getSeasonalAnime(2024, "winter", page = 1) }
        }

        @Test
        fun `getCurrentSeason falls back to JikanClient`() = runTest {
            coEvery { jikanClient.getCurrentSeasonAnime(page = 1) } returns sampleResponse

            val result = service.getCurrentSeason(page = 1)

            assertEquals(1, result.data.size)
            coVerify(exactly = 1) { jikanClient.getCurrentSeasonAnime(page = 1) }
        }

        @Test
        fun `searchGenres falls back to JikanClient`() = runTest {
            coEvery { jikanClient.getAnimeGenres() } returns sampleGenreResponse

            val result = service.searchGenres(q = "drama")

            assertEquals(1, result.size)
            assertEquals(8L, result[0].id)
            assertEquals("Drama", result[0].name)
            coVerify(exactly = 1) { jikanClient.getAnimeGenres() }
        }

        @Test
        fun `searchProducers falls back to JikanClient`() = runTest {
            coEvery { jikanClient.searchProducers(q = "mappa", page = 1, limit = 10) } returns sampleProducerResponse

            val result = service.searchProducers(q = "mappa", page = 1, limit = 10)

            assertEquals(1, result.size)
            assertEquals(569L, result[0].id)
            assertEquals("MAPPA", result[0].name)
            coVerify(exactly = 1) { jikanClient.searchProducers(q = "mappa", page = 1, limit = 10) }
        }
    }
}
