package com.anirec.domain.rating.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class StatsResponse(
    @JsonProperty("total_rated") val totalRated: Long,
    @JsonProperty("average_score") val averageScore: Double,
    @JsonProperty("favorite_genre") val favoriteGenre: String,
    @JsonProperty("genre_stats") val genreStats: List<GenreStatDto>,
    @JsonProperty("rating_distribution") val ratingDistribution: List<RatingDistributionDto>,
    @JsonProperty("top_studios") val topStudios: List<StudioStatDto>,
    @JsonProperty("monthly_history") val monthlyHistory: List<MonthlyHistoryDto>,
)

data class GenreStatDto(
    val genre: String,
    val count: Long,
    @JsonProperty("avg_score") val avgScore: Double,
)

data class RatingDistributionDto(
    val score: Int,
    val count: Long,
)

data class StudioStatDto(
    val studio: String,
    val count: Long,
    @JsonProperty("avg_score") val avgScore: Double,
)

data class MonthlyHistoryDto(
    val month: String,
    val count: Long,
)
