package com.anirec.domain.rating.repository

import com.anirec.domain.rating.entity.Rating
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface RatingRepository : JpaRepository<Rating, Long> {

    @EntityGraph(attributePaths = ["anime"])
    fun findByMemberIdAndAnimeId(memberId: String, animeId: Long): Rating?

    @EntityGraph(attributePaths = ["anime"])
    fun findAllByMemberIdOrderByUpdatedAtDesc(memberId: String): List<Rating>

    fun deleteByMemberIdAndAnimeId(memberId: String, animeId: Long)
}
