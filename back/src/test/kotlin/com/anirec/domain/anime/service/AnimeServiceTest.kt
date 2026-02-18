package com.anirec.domain.anime.service

import com.anirec.domain.anime.entity.Anime
import com.anirec.domain.anime.entity.Genre
import com.anirec.domain.anime.entity.Studio
import com.anirec.domain.anime.repository.AnimeRepository
import com.anirec.domain.anime.repository.GenreRepository
import com.anirec.domain.anime.repository.StudioRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnimeServiceTest {

    private val animeRepository: AnimeRepository = mockk()
    private val genreRepository: GenreRepository = mockk()
    private val studioRepository: StudioRepository = mockk()

    private val service = AnimeService(animeRepository, genreRepository, studioRepository)

    private val actionGenre = Genre(malId = 1, name = "Action", count = 5439, id = 1)
    private val comedyGenre = Genre(malId = 4, name = "Comedy", count = 7000, id = 2)
    private val dramaGenre = Genre(malId = 8, name = "Drama", count = 3000, id = 3)

    private val sunriseStudio = Studio(malId = 14, name = "Sunrise", id = 1)
    private val mappaStudio = Studio(malId = 569, name = "MAPPA", id = 2)

    private val sampleAnime = Anime(
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
        imageUrl = "url",
        largeImageUrl = "large_url",
        url = "https://myanimelist.net/anime/1/Cowboy_Bebop",
        id = 1,
    ).apply {
        genres.add(actionGenre)
        studios.add(sunriseStudio)
    }

    @Nested
    inner class Search {

        @Test
        fun `search returns paginated results from repository`() {
            val pageable = PageRequest.of(0, 25)
            val page = PageImpl(listOf(sampleAnime), pageable, 1)

            every {
                animeRepository.search(
                    type = "TV",
                    status = "airing",
                    genreMalIds = null,
                    producerMalIds = null,
                    orderBy = null,
                    sort = null,
                    pageable = pageable,
                )
            } returns page

            val result = service.search(page = 1, limit = 25, type = "TV", status = "airing")

            assertEquals(1, result.data.size)
            assertEquals("Cowboy Bebop", result.data[0].title)
            assertEquals(1, result.pagination?.currentPage)
            assertEquals(false, result.pagination?.hasNextPage)
        }

        @Test
        fun `search parses genres comma string to malId list`() {
            val pageable = PageRequest.of(0, 25)
            val page = PageImpl(listOf(sampleAnime), pageable, 1)

            every {
                animeRepository.search(
                    type = null,
                    status = null,
                    genreMalIds = listOf(1L, 4L),
                    producerMalIds = null,
                    orderBy = null,
                    sort = null,
                    pageable = pageable,
                )
            } returns page

            val result = service.search(page = 1, limit = 25, genres = "1,4")

            assertEquals(1, result.data.size)
            verify {
                animeRepository.search(
                    type = null,
                    status = null,
                    genreMalIds = listOf(1L, 4L),
                    producerMalIds = null,
                    orderBy = null,
                    sort = null,
                    pageable = pageable,
                )
            }
        }

        @Test
        fun `search parses producers comma string to malId list`() {
            val pageable = PageRequest.of(0, 25)
            val page = PageImpl(listOf(sampleAnime), pageable, 1)

            every {
                animeRepository.search(
                    type = null,
                    status = null,
                    genreMalIds = null,
                    producerMalIds = listOf(14L, 569L),
                    orderBy = null,
                    sort = null,
                    pageable = pageable,
                )
            } returns page

            val result = service.search(page = 1, limit = 25, producers = "14,569")

            assertEquals(1, result.data.size)
        }

        @Test
        fun `search returns empty result`() {
            val pageable = PageRequest.of(0, 25)
            val page = PageImpl<Anime>(emptyList(), pageable, 0)

            every {
                animeRepository.search(
                    type = null,
                    status = null,
                    genreMalIds = null,
                    producerMalIds = null,
                    orderBy = null,
                    sort = null,
                    pageable = pageable,
                )
            } returns page

            val result = service.search(page = 1, limit = 25)

            assertTrue(result.data.isEmpty())
            assertEquals(0, result.pagination?.items?.total)
        }
    }

    @Nested
    inner class GetTop {

        @Test
        fun `getTop returns top anime from repository`() {
            val pageable = PageRequest.of(0, 25)
            val page = PageImpl(listOf(sampleAnime), pageable, 1)

            every { animeRepository.findTop(pageable) } returns page

            val result = service.getTop(page = 1, limit = 25)

            assertEquals(1, result.data.size)
            assertEquals("Cowboy Bebop", result.data[0].title)
        }

        @Test
        fun `getTop returns empty result`() {
            val pageable = PageRequest.of(0, 25)
            val page = PageImpl<Anime>(emptyList(), pageable, 0)

            every { animeRepository.findTop(pageable) } returns page

            val result = service.getTop(page = 1, limit = 25)

            assertTrue(result.data.isEmpty())
        }
    }

    @Nested
    inner class GetSeasonal {

        @Test
        fun `getSeasonal returns seasonal anime from repository`() {
            val pageable = PageRequest.of(0, 25)
            val page = PageImpl(listOf(sampleAnime), pageable, 1)

            every { animeRepository.findBySeason(2024, "winter", pageable) } returns page

            val result = service.getSeasonal(year = 2024, season = "winter", page = 1, limit = 25)

            assertEquals(1, result.data.size)
            assertEquals("Cowboy Bebop", result.data[0].title)
        }
    }

    @Nested
    inner class GetCurrentSeason {

        @Test
        fun `getCurrentSeason delegates to getSeasonal with computed year and season`() {
            val pageable = PageRequest.of(0, 25)
            val page = PageImpl(listOf(sampleAnime), pageable, 1)

            // We can't mock LocalDate.now() easily, but we can verify the repository is called
            every { animeRepository.findBySeason(any(), any(), pageable) } returns page

            val result = service.getCurrentSeason(page = 1, limit = 25)

            assertEquals(1, result.data.size)
            verify { animeRepository.findBySeason(any(), any(), pageable) }
        }
    }

    @Nested
    inner class SearchGenres {

        @Test
        fun `searchGenres returns all genres when query is null`() {
            every { genreRepository.findAllByOrderByCountDesc() } returns listOf(actionGenre, comedyGenre, dramaGenre)

            val result = service.searchGenres(q = null)

            assertEquals(3, result.size)
            assertEquals(1L, result[0].id)
            assertEquals("Action", result[0].name)
            verify { genreRepository.findAllByOrderByCountDesc() }
        }

        @Test
        fun `searchGenres filters by query`() {
            every { genreRepository.findByNameContainingIgnoreCaseOrderByCountDesc("act") } returns listOf(actionGenre)

            val result = service.searchGenres(q = "act")

            assertEquals(1, result.size)
            assertEquals(1L, result[0].id)
            assertEquals("Action", result[0].name)
        }

        @Test
        fun `searchGenres returns all genres when query is blank`() {
            every { genreRepository.findAllByOrderByCountDesc() } returns listOf(actionGenre, comedyGenre)

            val result = service.searchGenres(q = "  ")

            assertEquals(2, result.size)
            verify { genreRepository.findAllByOrderByCountDesc() }
        }
    }

    @Nested
    inner class SearchProducers {

        @Test
        fun `searchProducers returns matching studios`() {
            every { studioRepository.findByNameContainingIgnoreCase("mappa") } returns listOf(mappaStudio)

            val result = service.searchProducers(q = "mappa", limit = 10)

            assertEquals(1, result.size)
            assertEquals(569L, result[0].id)
            assertEquals("MAPPA", result[0].name)
        }

        @Test
        fun `searchProducers returns empty list when query is null`() {
            val result = service.searchProducers(q = null)

            assertTrue(result.isEmpty())
        }

        @Test
        fun `searchProducers returns empty list when query is blank`() {
            val result = service.searchProducers(q = "  ")

            assertTrue(result.isEmpty())
        }

        @Test
        fun `searchProducers respects limit`() {
            val studios = (1..20).map { Studio(malId = it.toLong(), name = "Studio $it", id = it.toLong()) }
            every { studioRepository.findByNameContainingIgnoreCase("studio") } returns studios

            val result = service.searchProducers(q = "studio", limit = 5)

            assertEquals(5, result.size)
        }

        @Test
        fun `searchProducers uses default limit of 10`() {
            val studios = (1..20).map { Studio(malId = it.toLong(), name = "Studio $it", id = it.toLong()) }
            every { studioRepository.findByNameContainingIgnoreCase("studio") } returns studios

            val result = service.searchProducers(q = "studio")

            assertEquals(10, result.size)
        }
    }
}
