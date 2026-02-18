package com.anirec.domain.anime.repository

import com.anirec.domain.anime.entity.Studio
import org.springframework.data.jpa.repository.JpaRepository

interface StudioRepository : JpaRepository<Studio, Long> {

    fun findByNameContainingIgnoreCase(name: String): List<Studio>

    fun findByMalIdIn(malIds: List<Long>): List<Studio>
}
