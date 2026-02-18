package com.anirec.domain.anime.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "sync_progress")
class SyncProgress(
    @Column(unique = true, nullable = false, length = 100)
    val taskName: String,

    val lastProcessedPage: Int = 0,

    val totalPages: Int? = null,

    @Column(nullable = false, length = 20)
    val status: String = "IDLE",

    val startedAt: LocalDateTime? = null,

    val updatedAt: LocalDateTime? = null,

    val completedAt: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
)
