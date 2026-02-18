package com.anirec.domain.anime.service

import com.anirec.domain.anime.client.JikanClient
import com.anirec.domain.anime.dto.AnimeDto
import com.anirec.domain.anime.dto.JikanResponse
import com.anirec.domain.anime.entity.Studio
import com.anirec.domain.anime.entity.SyncProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDateTime

@Service
class AnimeDataSyncService(
    private val jikanClient: JikanClient,
    private val persistService: AnimeDataPersistService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val TASK_NAME = "anime-full-sync"
        private const val MAX_RETRIES = 5
        private const val INITIAL_BACKOFF_MS = 1000L
        private const val MAX_BACKOFF_MS = 30_000L
        private const val PAGE_DELAY_MS = 334L
    }

    fun getSyncProgress(): SyncProgress? =
        persistService.findSyncProgress(TASK_NAME)

    suspend fun syncGenres() {
        log.info("[AnimeSync] Starting genre sync")
        val response = jikanClient.getAnimeGenres()
        val saved = withContext(Dispatchers.IO) { persistService.upsertGenres(response.data) }
        log.info("[AnimeSync] Genre sync completed. {} genres saved", saved.size)
    }

    suspend fun syncAllAnime(startPage: Int?) {
        val progress = withContext(Dispatchers.IO) { persistService.findSyncProgress(TASK_NAME) }

        if (progress != null && progress.status == "RUNNING") {
            log.warn("[AnimeSync] Sync already running. Use startPage parameter to force restart.")
            return
        }

        val resumePage = startPage
            ?: ((progress?.lastProcessedPage ?: 0) + 1).coerceAtLeast(1)

        log.info("[AnimeSync] Starting anime sync from page {}", resumePage)

        withContext(Dispatchers.IO) {
            persistService.saveSyncProgress(
                existing = progress,
                taskName = TASK_NAME,
                status = "RUNNING",
                lastProcessedPage = resumePage - 1,
                startedAt = LocalDateTime.now(),
            )
        }

        val genreLookup = withContext(Dispatchers.IO) {
            persistService.findAllGenres().associateBy({ it.malId }, { it }).toMutableMap()
        }
        val studioLookup = withContext(Dispatchers.IO) {
            persistService.findAllStudios().associateBy({ it.malId }, { it }).toMutableMap()
        }
        val failedPages = mutableListOf<Int>()

        var currentPage = resumePage
        var hasNextPage = true

        while (hasNextPage) {
            try {
                val response = fetchPageWithBackoff(currentPage)

                val newStudios = extractNewStudios(response.data, studioLookup)
                if (newStudios.isNotEmpty()) {
                    val saved = withContext(Dispatchers.IO) { persistService.saveNewStudios(newStudios) }
                    saved.forEach { studioLookup[it.malId] = it }
                    log.info("[AnimeSync] Saved {} new studios", newStudios.size)
                }

                withContext(Dispatchers.IO) {
                    persistService.upsertAnimeBatch(response.data, genreLookup, studioLookup)
                }

                withContext(Dispatchers.IO) {
                    persistService.saveSyncProgress(
                        existing = persistService.findSyncProgress(TASK_NAME),
                        taskName = TASK_NAME,
                        status = "RUNNING",
                        lastProcessedPage = currentPage,
                        totalPages = response.pagination?.lastVisiblePage,
                    )
                }

                val total = response.pagination?.lastVisiblePage ?: currentPage
                log.info("[AnimeSync] Page {}/{} processed ({} anime)", currentPage, total, response.data.size)

                hasNextPage = response.pagination?.hasNextPage ?: false
                currentPage++

                if (hasNextPage) {
                    delay(PAGE_DELAY_MS)
                }
            } catch (e: Exception) {
                log.error("[AnimeSync] Error on page {}: {}", currentPage, e.message)
                failedPages.add(currentPage)
                currentPage++
                delay(PAGE_DELAY_MS)
            }
        }

        withContext(Dispatchers.IO) {
            persistService.saveSyncProgress(
                existing = persistService.findSyncProgress(TASK_NAME),
                taskName = TASK_NAME,
                status = "COMPLETED",
                lastProcessedPage = currentPage - 1,
                completedAt = LocalDateTime.now(),
            )
        }

        if (failedPages.isNotEmpty()) {
            log.warn("[AnimeSync] Completed with {} failed pages: {}", failedPages.size, failedPages)
        } else {
            log.info("[AnimeSync] Completed successfully. Total pages: {}", currentPage - 1)
        }
    }

    private suspend fun fetchPageWithBackoff(page: Int): JikanResponse {
        var attempt = 0
        var delayMs = INITIAL_BACKOFF_MS
        while (true) {
            try {
                return jikanClient.searchAnime(page = page, limit = 25, orderBy = "mal_id", sort = "asc")
            } catch (e: WebClientResponseException) {
                if (e.statusCode.value() == 429 && attempt < MAX_RETRIES) {
                    log.warn("[AnimeSync] 429 Rate limited on page {}. Retry {} after {}ms", page, attempt + 1, delayMs)
                    delay(delayMs)
                    delayMs = (delayMs * 2).coerceAtMost(MAX_BACKOFF_MS)
                    attempt++
                } else {
                    throw e
                }
            }
        }
    }

    private fun extractNewStudios(
        animeDtos: List<AnimeDto>,
        studioLookup: Map<Long, Studio>,
    ): List<Studio> =
        animeDtos.flatMap { it.studios ?: emptyList() }
            .filter { it.malId !in studioLookup }
            .distinctBy { it.malId }
            .map { Studio(malId = it.malId, name = it.name) }
}
