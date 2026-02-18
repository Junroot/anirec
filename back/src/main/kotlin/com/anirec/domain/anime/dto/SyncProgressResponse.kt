package com.anirec.domain.anime.dto

import com.anirec.domain.anime.entity.SyncProgress
import java.time.LocalDateTime

data class SyncProgressResponse(
    val status: String,
    val lastProcessedPage: Int,
    val totalPages: Int?,
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
)

fun SyncProgress.toResponse() = SyncProgressResponse(
    status = status,
    lastProcessedPage = lastProcessedPage,
    totalPages = totalPages,
    startedAt = startedAt,
    completedAt = completedAt,
)
