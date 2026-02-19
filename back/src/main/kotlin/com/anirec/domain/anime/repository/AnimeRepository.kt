package com.anirec.domain.anime.repository

import com.anirec.domain.anime.entity.Anime
import org.springframework.data.jpa.repository.JpaRepository

interface AnimeRepository : JpaRepository<Anime, Long>, AnimeRepositoryCustom {

    fun findByMalId(malId: Long): Anime?

    fun findByMalIdIn(malIds: List<Long>): List<Anime>
}
