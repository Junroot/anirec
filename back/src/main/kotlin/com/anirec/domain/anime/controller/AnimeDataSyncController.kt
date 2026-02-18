package com.anirec.domain.anime.controller

import com.anirec.domain.anime.dto.SyncProgressResponse
import com.anirec.domain.anime.dto.toResponse
import com.anirec.domain.anime.service.AnimeDataSyncService
import com.anirec.global.exception.SyncAlreadyRunningException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/sync")
class AnimeDataSyncController(
    private val animeDataSyncService: AnimeDataSyncService,
) {
    private val syncScope = CoroutineScope(SupervisorJob())

    @GetMapping("/status")
    suspend fun getSyncStatus(): SyncProgressResponse {
        val progress = withContext(Dispatchers.IO) {
            animeDataSyncService.getSyncProgress()
        }
        return progress?.toResponse()
            ?: SyncProgressResponse(
                status = "IDLE",
                lastProcessedPage = 0,
                totalPages = null,
                startedAt = null,
                completedAt = null,
            )
    }

    @PostMapping("/genres")
    suspend fun syncGenres(): Map<String, String> {
        animeDataSyncService.syncGenres()
        return mapOf("status" to "completed", "message" to "Genre sync completed")
    }

    @PostMapping("/anime")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun syncAnime(
        @RequestParam(required = false) startPage: Int?,
    ): Map<String, String> {
        val progress = withContext(Dispatchers.IO) {
            animeDataSyncService.getSyncProgress()
        }
        if (progress != null && progress.status == "RUNNING") {
            throw SyncAlreadyRunningException()
        }
        syncScope.launch {
            animeDataSyncService.syncAllAnime(startPage)
        }
        return mapOf("status" to "accepted", "message" to "Anime sync started in background")
    }
}
