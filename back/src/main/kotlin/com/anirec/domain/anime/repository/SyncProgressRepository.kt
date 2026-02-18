package com.anirec.domain.anime.repository

import com.anirec.domain.anime.entity.SyncProgress
import org.springframework.data.jpa.repository.JpaRepository

interface SyncProgressRepository : JpaRepository<SyncProgress, Long> {

    fun findByTaskName(taskName: String): SyncProgress?
}
