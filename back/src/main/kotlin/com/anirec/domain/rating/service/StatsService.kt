package com.anirec.domain.rating.service

import com.anirec.domain.rating.dto.GenreStatDto
import com.anirec.domain.rating.dto.MonthlyHistoryDto
import com.anirec.domain.rating.dto.RatingDistributionDto
import com.anirec.domain.rating.dto.StatsResponse
import com.anirec.domain.rating.dto.StudioStatDto
import com.anirec.domain.rating.repository.RatingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Service
class StatsService(
    private val ratingRepository: RatingRepository,
) {

    suspend fun getMyStats(memberId: String): StatsResponse =
        withContext(Dispatchers.IO) {
            val totalRated = ratingRepository.countByMemberId(memberId)
            if (totalRated == 0L) return@withContext emptyStats()

            val averageScore = ratingRepository.findAverageScoreByMemberId(memberId)

            val genreStats = ratingRepository.findGenreStatsByMemberId(memberId).map {
                GenreStatDto(
                    genre = it[0] as String,
                    count = (it[1] as Number).toLong(),
                    avgScore = (it[2] as Number).toDouble(),
                )
            }

            val ratingDistribution = ratingRepository.findRatingDistributionByMemberId(memberId).map {
                RatingDistributionDto(
                    score = (it[0] as Number).toInt(),
                    count = (it[1] as Number).toLong(),
                )
            }

            val topStudios = ratingRepository.findStudioStatsByMemberId(memberId).take(6).map {
                StudioStatDto(
                    studio = it[0] as String,
                    count = (it[1] as Number).toLong(),
                    avgScore = (it[2] as Number).toDouble(),
                )
            }

            val since = LocalDateTime.now().minusMonths(12).withDayOfMonth(1).toLocalDate().atStartOfDay()
            val monthlyHistory = ratingRepository.findMonthlyHistoryByMemberId(memberId, since).map {
                MonthlyHistoryDto(
                    month = "%04d-%02d".format((it[0] as Number).toInt(), (it[1] as Number).toInt()),
                    count = (it[2] as Number).toLong(),
                )
            }

            StatsResponse(
                totalRated = totalRated,
                averageScore = (averageScore * 10).roundToInt() / 10.0,
                favoriteGenre = genreStats.firstOrNull()?.genre ?: "",
                genreStats = genreStats,
                ratingDistribution = ratingDistribution,
                topStudios = topStudios,
                monthlyHistory = monthlyHistory,
            )
        }

    private fun emptyStats() = StatsResponse(
        totalRated = 0,
        averageScore = 0.0,
        favoriteGenre = "",
        genreStats = emptyList(),
        ratingDistribution = emptyList(),
        topStudios = emptyList(),
        monthlyHistory = emptyList(),
    )
}
