package com.anirec.domain.anime.service

import com.anirec.domain.anime.client.JikanClient
import com.anirec.domain.anime.dto.*
import com.anirec.domain.anime.entity.Anime
import com.anirec.domain.anime.entity.Genre
import com.anirec.domain.anime.entity.Studio
import com.anirec.domain.anime.entity.SyncProgress
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AnimeDataSyncServiceTest {

    private val jikanClient: JikanClient = mockk()
    private val persistService: AnimeDataPersistService = mockk()

    private val service = AnimeDataSyncService(jikanClient, persistService)

    private val sampleGenreResponse = JikanGenreResponse(
        data = listOf(
            GenreDto(malId = 1, name = "Action", count = 5000),
            GenreDto(malId = 4, name = "Comedy", count = 3000),
        ),
    )

    private fun sampleAnimeDto(malId: Long, title: String) = AnimeDto(
        malId = malId,
        title = title,
        titleJapanese = null,
        synopsis = null,
        score = 8.0,
        scoredBy = 100000,
        rank = 1,
        popularity = 1,
        members = 500000,
        episodes = 12,
        status = "Finished Airing",
        type = "TV",
        season = "winter",
        year = 2024,
        genres = listOf(AnimeDto.MalEntity(1, "Action")),
        studios = listOf(AnimeDto.MalEntity(14, "Sunrise")),
        images = AnimeDto.Images(AnimeDto.Images.JpgImage("url", "large_url")),
        aired = AnimeDto.Aired("2024-01-01", "2024-03-25"),
        url = "https://myanimelist.net/anime/$malId",
    )

    private fun sampleJikanResponse(
        data: List<AnimeDto>,
        currentPage: Int = 1,
        lastPage: Int = 1,
        hasNextPage: Boolean = false,
    ) = JikanResponse(
        pagination = JikanResponse.Pagination(
            lastVisiblePage = lastPage,
            hasNextPage = hasNextPage,
            currentPage = currentPage,
            items = JikanResponse.PaginationItems(count = data.size, total = data.size, perPage = 25),
        ),
        data = data,
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    inner class SyncGenres {

        @Test
        fun `delegates genre upsert to persistService`() = runTest {
            coEvery { jikanClient.getAnimeGenres() } returns sampleGenreResponse
            every { persistService.upsertGenres(any()) } returns listOf(
                Genre(malId = 1, name = "Action", count = 5000, id = 10),
                Genre(malId = 4, name = "Comedy", count = 3000, id = 11),
            )

            service.syncGenres()

            coVerify(exactly = 1) { jikanClient.getAnimeGenres() }
            verify(exactly = 1) {
                persistService.upsertGenres(match { dtos ->
                    dtos.size == 2 && dtos[0].malId == 1L && dtos[1].malId == 4L
                })
            }
        }
    }

    @Nested
    inner class SyncAllAnime {

        private fun setupCommonMocks() {
            every { persistService.saveSyncProgress(any(), any(), any(), any(), any(), any(), any()) } answers {
                SyncProgress(taskName = arg(1), status = arg(2), id = 1)
            }
            every { persistService.findAllGenres() } returns listOf(Genre(malId = 1, name = "Action", id = 10))
            every { persistService.findAllStudios() } returns listOf(Studio(malId = 14, name = "Sunrise", id = 20))
            every { persistService.saveNewStudios(any()) } answers { firstArg() }
            every { persistService.upsertAnimeBatch(any(), any(), any()) } just runs
        }

        @Test
        fun `processes single page and completes`() = runTest {
            every { persistService.findSyncProgress("anime-full-sync") } returns null
            setupCommonMocks()

            coEvery { jikanClient.searchAnime(page = 1, limit = 25, orderBy = "mal_id", sort = "asc") } returns
                sampleJikanResponse(listOf(sampleAnimeDto(1, "Anime 1")), hasNextPage = false)

            service.syncAllAnime(null)

            verify(atLeast = 1) { persistService.upsertAnimeBatch(any(), any(), any()) }
            verify {
                persistService.saveSyncProgress(any(), "anime-full-sync", "COMPLETED", any(), any(), any(), any())
            }
        }

        @Test
        fun `resumes from last processed page`() = runTest {
            every { persistService.findSyncProgress("anime-full-sync") } returns
                SyncProgress(taskName = "anime-full-sync", lastProcessedPage = 5, status = "FAILED", id = 1)
            setupCommonMocks()

            coEvery { jikanClient.searchAnime(page = 6, limit = 25, orderBy = "mal_id", sort = "asc") } returns
                sampleJikanResponse(listOf(sampleAnimeDto(126, "Anime 126")), currentPage = 6, lastPage = 6, hasNextPage = false)

            service.syncAllAnime(null)

            coVerify { jikanClient.searchAnime(page = 6, limit = 25, orderBy = "mal_id", sort = "asc") }
            coVerify(exactly = 0) { jikanClient.searchAnime(page = 1, limit = 25, orderBy = "mal_id", sort = "asc") }
        }

        @Test
        fun `skips when already running`() = runTest {
            every { persistService.findSyncProgress("anime-full-sync") } returns
                SyncProgress(taskName = "anime-full-sync", status = "RUNNING", id = 1)

            service.syncAllAnime(null)

            coVerify(exactly = 0) { jikanClient.searchAnime(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }

        @Test
        fun `startPage parameter overrides resume page`() = runTest {
            every { persistService.findSyncProgress("anime-full-sync") } returns
                SyncProgress(taskName = "anime-full-sync", lastProcessedPage = 5, status = "FAILED", id = 1)
            setupCommonMocks()

            coEvery { jikanClient.searchAnime(page = 3, limit = 25, orderBy = "mal_id", sort = "asc") } returns
                sampleJikanResponse(listOf(sampleAnimeDto(51, "Anime 51")), currentPage = 3, lastPage = 3, hasNextPage = false)

            service.syncAllAnime(startPage = 3)

            coVerify { jikanClient.searchAnime(page = 3, limit = 25, orderBy = "mal_id", sort = "asc") }
        }

        @Test
        fun `continues to next page on non-429 error`() = runTest {
            every { persistService.findSyncProgress("anime-full-sync") } returns null
            setupCommonMocks()

            coEvery { jikanClient.searchAnime(page = 1, limit = 25, orderBy = "mal_id", sort = "asc") } throws
                RuntimeException("Network error")
            coEvery { jikanClient.searchAnime(page = 2, limit = 25, orderBy = "mal_id", sort = "asc") } returns
                sampleJikanResponse(listOf(sampleAnimeDto(26, "Anime 26")), currentPage = 2, lastPage = 2, hasNextPage = false)

            service.syncAllAnime(null)

            coVerify { jikanClient.searchAnime(page = 1, limit = 25, orderBy = "mal_id", sort = "asc") }
            coVerify { jikanClient.searchAnime(page = 2, limit = 25, orderBy = "mal_id", sort = "asc") }
        }

        @Test
        fun `saves new studios from anime data`() = runTest {
            every { persistService.findSyncProgress("anime-full-sync") } returns null
            setupCommonMocks()
            every { persistService.findAllStudios() } returns emptyList()

            val animeWithNewStudio = sampleAnimeDto(1, "Anime 1").copy(
                studios = listOf(AnimeDto.MalEntity(999, "New Studio")),
            )
            coEvery { jikanClient.searchAnime(page = 1, limit = 25, orderBy = "mal_id", sort = "asc") } returns
                sampleJikanResponse(listOf(animeWithNewStudio), hasNextPage = false)
            every { persistService.saveNewStudios(any()) } answers {
                firstArg<List<Studio>>().mapIndexed { i, s ->
                    Studio(malId = s.malId, name = s.name, id = (100 + i).toLong())
                }
            }

            service.syncAllAnime(null)

            verify {
                persistService.saveNewStudios(match { studios ->
                    studios.size == 1 && studios[0].malId == 999L && studios[0].name == "New Studio"
                })
            }
        }

        @Test
        fun `delegates anime upsert to persistService with lookup maps`() = runTest {
            every { persistService.findSyncProgress("anime-full-sync") } returns null
            setupCommonMocks()

            val anime1 = sampleAnimeDto(1, "Anime 1")
            coEvery { jikanClient.searchAnime(page = 1, limit = 25, orderBy = "mal_id", sort = "asc") } returns
                sampleJikanResponse(listOf(anime1), hasNextPage = false)

            service.syncAllAnime(null)

            verify {
                persistService.upsertAnimeBatch(
                    match { it.size == 1 && it[0].malId == 1L },
                    match { it.containsKey(1L) },
                    match { it.containsKey(14L) },
                )
            }
        }
    }
}
