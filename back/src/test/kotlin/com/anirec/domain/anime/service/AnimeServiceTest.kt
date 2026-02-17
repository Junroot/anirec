package com.anirec.domain.anime.service

import com.anirec.domain.anime.client.JikanClient
import com.anirec.domain.anime.dto.AnimeDto
import com.anirec.domain.anime.dto.JikanResponse
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

    @Nested
    inner class WithCacheService {
        private val service = AnimeService(jikanClient, animeCacheService)

        @Test
        fun `search delegates to AnimeCacheService`() = runTest {
            coEvery { animeCacheService.searchAnime(query = "bebop", page = 1) } returns sampleResponse

            val result = service.search(query = "bebop", page = 1)

            assertEquals(1, result.data.size)
            assertEquals("Cowboy Bebop", result.data[0].title)
            coVerify(exactly = 1) { animeCacheService.searchAnime(query = "bebop", page = 1) }
            coVerify(exactly = 0) { jikanClient.searchAnime(any(), any(), any(), any(), any(), any(), any(), any()) }
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
    }

    @Nested
    inner class WithoutCacheService {
        private val service = AnimeService(jikanClient, null)

        @Test
        fun `search falls back to JikanClient`() = runTest {
            coEvery { jikanClient.searchAnime(query = "bebop", page = 1) } returns sampleResponse

            val result = service.search(query = "bebop", page = 1)

            assertEquals(1, result.data.size)
            assertEquals("Cowboy Bebop", result.data[0].title)
            coVerify(exactly = 1) { jikanClient.searchAnime(query = "bebop", page = 1) }
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
    }
}
