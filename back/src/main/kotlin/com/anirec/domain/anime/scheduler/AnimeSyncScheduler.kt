package com.anirec.domain.anime.scheduler

import com.anirec.domain.anime.service.AnimeDataSyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["app.sync.enabled"], havingValue = "true")
class AnimeSyncScheduler(
    private val animeDataSyncService: AnimeDataSyncService,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Scheduled(cron = "\${app.sync.season-cron}")
    fun scheduleSeasonSync() {
        log.info("[Scheduler] Starting current season sync")
        syncScope.launch {
            try {
                animeDataSyncService.syncCurrentSeason()
            } catch (e: Exception) {
                log.error("[Scheduler] Season sync failed", e)
            }
        }
    }

    @Scheduled(cron = "\${app.sync.full-cron}")
    fun scheduleFullSync() {
        log.info("[Scheduler] Starting full anime resync")
        syncScope.launch {
            try {
                animeDataSyncService.syncAllAnime(startPage = 1)
            } catch (e: Exception) {
                log.error("[Scheduler] Full sync failed", e)
            }
        }
    }
}
