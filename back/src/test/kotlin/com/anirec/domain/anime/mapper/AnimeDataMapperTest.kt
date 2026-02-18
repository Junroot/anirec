package com.anirec.domain.anime.mapper

import com.anirec.domain.anime.dto.AnimeDto
import com.anirec.domain.anime.dto.GenreDto
import com.anirec.domain.anime.entity.Genre
import com.anirec.domain.anime.entity.Studio
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnimeDataMapperTest {

    private val actionGenre = Genre(malId = 1, name = "Action", count = 5000, id = 10)
    private val comedyGenre = Genre(malId = 4, name = "Comedy", count = 3000, id = 11)
    private val sunriseStudio = Studio(malId = 14, name = "Sunrise", id = 20)

    private val genreLookup = mapOf(1L to actionGenre, 4L to comedyGenre)
    private val studioLookup = mapOf(14L to sunriseStudio)

    private val sampleDto = AnimeDto(
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
        genres = listOf(
            AnimeDto.MalEntity(1, "Action"),
            AnimeDto.MalEntity(4, "Comedy"),
        ),
        studios = listOf(AnimeDto.MalEntity(14, "Sunrise")),
        images = AnimeDto.Images(
            AnimeDto.Images.JpgImage("https://img.jpg", "https://img_large.jpg"),
        ),
        aired = AnimeDto.Aired("1998-04-03T00:00:00+00:00", "1999-04-24T00:00:00+00:00"),
        url = "https://myanimelist.net/anime/1/Cowboy_Bebop",
    )

    @Nested
    inner class ToEntity {

        @Test
        fun `maps all fields correctly for new entity`() {
            val entity = AnimeDataMapper.toEntity(sampleDto, 0, genreLookup, studioLookup)

            assertEquals(1L, entity.malId)
            assertEquals("Cowboy Bebop", entity.title)
            assertEquals("カウボーイビバップ", entity.titleJapanese)
            assertEquals("A space bounty hunter crew.", entity.synopsis)
            assertEquals(8.78, entity.score)
            assertEquals(900000L, entity.scoredBy)
            assertEquals(28, entity.rank)
            assertEquals(39, entity.popularity)
            assertEquals(1800000L, entity.members)
            assertEquals(26, entity.episodes)
            assertEquals("Finished Airing", entity.status)
            assertEquals("TV", entity.type)
            assertEquals("spring", entity.season)
            assertEquals(1998, entity.year)
            assertEquals("https://img.jpg", entity.imageUrl)
            assertEquals("https://img_large.jpg", entity.largeImageUrl)
            assertEquals(LocalDate.of(1998, 4, 3), entity.airedFrom)
            assertEquals(LocalDate.of(1999, 4, 24), entity.airedTo)
            assertEquals("https://myanimelist.net/anime/1/Cowboy_Bebop", entity.url)
            assertEquals(0L, entity.id)
        }

        @Test
        fun `preserves existingId for update`() {
            val entity = AnimeDataMapper.toEntity(sampleDto, 42, genreLookup, studioLookup)

            assertEquals(42L, entity.id)
        }

        @Test
        fun `resolves genres from lookup`() {
            val entity = AnimeDataMapper.toEntity(sampleDto, 0, genreLookup, studioLookup)

            assertEquals(2, entity.genres.size)
            assertTrue(entity.genres.any { it.malId == 1L })
            assertTrue(entity.genres.any { it.malId == 4L })
        }

        @Test
        fun `resolves studios from lookup`() {
            val entity = AnimeDataMapper.toEntity(sampleDto, 0, genreLookup, studioLookup)

            assertEquals(1, entity.studios.size)
            assertTrue(entity.studios.any { it.malId == 14L })
        }

        @Test
        fun `skips genres not in lookup`() {
            val dtoWithUnknownGenre = sampleDto.copy(
                genres = listOf(AnimeDto.MalEntity(999, "Unknown")),
            )
            val entity = AnimeDataMapper.toEntity(dtoWithUnknownGenre, 0, genreLookup, studioLookup)

            assertTrue(entity.genres.isEmpty())
        }

        @Test
        fun `handles null optional fields`() {
            val minimalDto = AnimeDto(
                malId = 2,
                title = "Test",
                titleJapanese = null,
                synopsis = null,
                score = null,
                scoredBy = null,
                rank = null,
                popularity = null,
                members = null,
                episodes = null,
                status = null,
                type = null,
                season = null,
                year = null,
                genres = null,
                studios = null,
                images = null,
                aired = null,
                url = null,
            )
            val entity = AnimeDataMapper.toEntity(minimalDto, 0, genreLookup, studioLookup)

            assertEquals(2L, entity.malId)
            assertEquals("Test", entity.title)
            assertNull(entity.titleJapanese)
            assertNull(entity.score)
            assertNull(entity.imageUrl)
            assertNull(entity.airedFrom)
            assertNull(entity.airedTo)
            assertTrue(entity.genres.isEmpty())
            assertTrue(entity.studios.isEmpty())
        }
    }

    @Nested
    inner class ToGenreEntity {

        @Test
        fun `maps genre dto to entity for new genre`() {
            val dto = GenreDto(malId = 1, name = "Action", count = 5000)
            val entity = AnimeDataMapper.toGenreEntity(dto, 0)

            assertEquals(1L, entity.malId)
            assertEquals("Action", entity.name)
            assertEquals(5000, entity.count)
            assertEquals(0L, entity.id)
        }

        @Test
        fun `preserves existingId for update`() {
            val dto = GenreDto(malId = 1, name = "Action", count = 5000)
            val entity = AnimeDataMapper.toGenreEntity(dto, 10)

            assertEquals(10L, entity.id)
        }
    }

    @Nested
    inner class ParseDate {

        @Test
        fun `parses ISO date with timezone`() {
            val result = AnimeDataMapper.parseDate("1998-04-03T00:00:00+00:00")
            assertEquals(LocalDate.of(1998, 4, 3), result)
        }

        @Test
        fun `parses plain date string`() {
            val result = AnimeDataMapper.parseDate("2024-01-15")
            assertEquals(LocalDate.of(2024, 1, 15), result)
        }

        @Test
        fun `returns null for null input`() {
            assertNull(AnimeDataMapper.parseDate(null))
        }

        @Test
        fun `returns null for invalid date`() {
            assertNull(AnimeDataMapper.parseDate("not-a-date"))
        }

        @Test
        fun `returns null for empty string`() {
            assertNull(AnimeDataMapper.parseDate(""))
        }
    }
}
