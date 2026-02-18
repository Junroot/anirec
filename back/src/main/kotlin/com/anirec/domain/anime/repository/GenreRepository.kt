package com.anirec.domain.anime.repository

import com.anirec.domain.anime.entity.Genre
import org.springframework.data.jpa.repository.JpaRepository

interface GenreRepository : JpaRepository<Genre, Long> {

    fun findAllByOrderByCountDesc(): List<Genre>

    fun findByNameContainingIgnoreCaseOrderByCountDesc(name: String): List<Genre>

    fun findByMalIdIn(malIds: List<Long>): List<Genre>
}
