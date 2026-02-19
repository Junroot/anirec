package com.anirec.domain.rating.service

import com.anirec.domain.rating.repository.RatingRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class StatsServiceTest {

    private val ratingRepository: RatingRepository = mockk()
    private val service = StatsService(ratingRepository)

    private val memberId = "user-001"

    @Nested
    inner class GetMyStats {

        @Test
        fun `returns stats for user with ratings`() = runTest {
            every { ratingRepository.countByMemberId(memberId) } returns 5L
            every { ratingRepository.findAverageScoreByMemberId(memberId) } returns 8.24
            every { ratingRepository.findGenreStatsByMemberId(memberId) } returns listOf(
                arrayOf<Any>("Action", 4L, 8.5),
                arrayOf<Any>("Comedy", 2L, 7.0),
            )
            every { ratingRepository.findRatingDistributionByMemberId(memberId) } returns listOf(
                arrayOf<Any>(7, 1L),
                arrayOf<Any>(8, 2L),
                arrayOf<Any>(9, 2L),
            )
            every { ratingRepository.findStudioStatsByMemberId(memberId) } returns listOf(
                arrayOf<Any>("MAPPA", 3L, 8.7),
                arrayOf<Any>("ufotable", 2L, 9.0),
            )
            every { ratingRepository.findMonthlyHistoryByMemberId(memberId, any<LocalDateTime>()) } returns listOf(
                arrayOf<Any>(2025, 11, 2L),
                arrayOf<Any>(2025, 12, 3L),
            )

            val result = service.getMyStats(memberId)

            assertEquals(5L, result.totalRated)
            assertEquals(8.2, result.averageScore)
            assertEquals("Action", result.favoriteGenre)

            assertEquals(2, result.genreStats.size)
            assertEquals("Action", result.genreStats[0].genre)
            assertEquals(4L, result.genreStats[0].count)
            assertEquals(8.5, result.genreStats[0].avgScore)

            assertEquals(3, result.ratingDistribution.size)
            assertEquals(7, result.ratingDistribution[0].score)
            assertEquals(1L, result.ratingDistribution[0].count)

            assertEquals(2, result.topStudios.size)
            assertEquals("MAPPA", result.topStudios[0].studio)
            assertEquals(3L, result.topStudios[0].count)

            assertEquals(2, result.monthlyHistory.size)
            assertEquals("2025-11", result.monthlyHistory[0].month)
            assertEquals(2L, result.monthlyHistory[0].count)
            assertEquals("2025-12", result.monthlyHistory[1].month)
            assertEquals(3L, result.monthlyHistory[1].count)
        }

        @Test
        fun `returns empty stats for user with no ratings`() = runTest {
            every { ratingRepository.countByMemberId(memberId) } returns 0L

            val result = service.getMyStats(memberId)

            assertEquals(0L, result.totalRated)
            assertEquals(0.0, result.averageScore)
            assertEquals("", result.favoriteGenre)
            assertEquals(emptyList(), result.genreStats)
            assertEquals(emptyList(), result.ratingDistribution)
            assertEquals(emptyList(), result.topStudios)
            assertEquals(emptyList(), result.monthlyHistory)

            verify(exactly = 0) { ratingRepository.findAverageScoreByMemberId(any()) }
            verify(exactly = 0) { ratingRepository.findGenreStatsByMemberId(any()) }
            verify(exactly = 0) { ratingRepository.findRatingDistributionByMemberId(any()) }
            verify(exactly = 0) { ratingRepository.findStudioStatsByMemberId(any()) }
            verify(exactly = 0) { ratingRepository.findMonthlyHistoryByMemberId(any(), any()) }
        }
    }
}
