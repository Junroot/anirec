package com.anirec.domain.anime.controller

import com.anirec.domain.anime.service.AnimeDataSyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.http.HttpStatus
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

    @PostMapping("/genres")
    suspend fun syncGenres(): Map<String, String> {
        animeDataSyncService.syncGenres()
        return mapOf("status" to "completed", "message" to "Genre sync completed")
    }

    @PostMapping("/anime")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun syncAnime(
        @RequestParam(required = false) startPage: Int?,
    ): Map<String, String> {
        syncScope.launch {
            animeDataSyncService.syncAllAnime(startPage)
        }
        return mapOf("status" to "accepted", "message" to "Anime sync started in background")
    }
}
