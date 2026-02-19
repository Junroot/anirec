package com.anirec.domain.rating.repository

import com.anirec.domain.rating.entity.Rating
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface RatingRepository : JpaRepository<Rating, Long> {

    @EntityGraph(attributePaths = ["anime"])
    fun findByMemberIdAndAnimeId(memberId: String, animeId: Long): Rating?

    @EntityGraph(attributePaths = ["anime"])
    fun findAllByMemberIdOrderByUpdatedAtDesc(memberId: String): List<Rating>

    @Transactional
    fun deleteByMemberIdAndAnimeId(memberId: String, animeId: Long)

    fun countByMemberId(memberId: String): Long

    @Query("SELECT COALESCE(AVG(CAST(r.score AS double)), 0.0) FROM Rating r WHERE r.member.id = :memberId")
    fun findAverageScoreByMemberId(memberId: String): Double

    @Query("SELECT r.score, COUNT(r) FROM Rating r WHERE r.member.id = :memberId GROUP BY r.score ORDER BY r.score")
    fun findRatingDistributionByMemberId(memberId: String): List<Array<Any>>

    @Query("SELECT g.name, COUNT(r), AVG(CAST(r.score AS double)) FROM Rating r JOIN r.anime a JOIN a.genres g WHERE r.member.id = :memberId GROUP BY g.name ORDER BY COUNT(r) DESC")
    fun findGenreStatsByMemberId(memberId: String): List<Array<Any>>

    @Query("SELECT s.name, COUNT(r), AVG(CAST(r.score AS double)) FROM Rating r JOIN r.anime a JOIN a.studios s WHERE r.member.id = :memberId GROUP BY s.name ORDER BY COUNT(r) DESC")
    fun findStudioStatsByMemberId(memberId: String): List<Array<Any>>

    @Query("SELECT YEAR(r.updatedAt), MONTH(r.updatedAt), COUNT(r) FROM Rating r WHERE r.member.id = :memberId AND r.updatedAt >= :since GROUP BY YEAR(r.updatedAt), MONTH(r.updatedAt) ORDER BY YEAR(r.updatedAt), MONTH(r.updatedAt)")
    fun findMonthlyHistoryByMemberId(memberId: String, since: LocalDateTime): List<Array<Any>>
}
