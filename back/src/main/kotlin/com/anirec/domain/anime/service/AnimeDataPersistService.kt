package com.anirec.domain.anime.service

import com.anirec.domain.anime.dto.AnimeDto
import com.anirec.domain.anime.dto.GenreDto
import com.anirec.domain.anime.entity.Genre
import com.anirec.domain.anime.entity.Studio
import com.anirec.domain.anime.entity.SyncProgress
import com.anirec.domain.anime.mapper.AnimeDataMapper
import com.anirec.domain.anime.repository.AnimeRepository
import com.anirec.domain.anime.repository.GenreRepository
import com.anirec.domain.anime.repository.StudioRepository
import com.anirec.domain.anime.repository.SyncProgressRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AnimeDataPersistService(
    private val animeRepository: AnimeRepository,
    private val genreRepository: GenreRepository,
    private val studioRepository: StudioRepository,
    private val syncProgressRepository: SyncProgressRepository,
) {

    @Transactional
    fun upsertGenres(genreDtos: List<GenreDto>): List<Genre> {
        val existingGenres = genreRepository.findAll().associateBy { it.malId }

        val genresToSave = genreDtos.map { dto ->
            val existingId = existingGenres[dto.malId]?.id ?: 0
            AnimeDataMapper.toGenreEntity(dto, existingId)
        }

        return genreRepository.saveAll(genresToSave)
    }

    @Transactional
    fun upsertAnimeBatch(
        animeDtos: List<AnimeDto>,
        genreLookup: Map<Long, Genre>,
        studioLookup: Map<Long, Studio>,
    ) {
        val malIds = animeDtos.map { it.malId }
        val existingAnime = animeRepository.findByMalIdIn(malIds).associateBy { it.malId }

        val entities = animeDtos.map { dto ->
            val existingId = existingAnime[dto.malId]?.id ?: 0
            AnimeDataMapper.toEntity(dto, existingId, genreLookup, studioLookup)
        }

        animeRepository.saveAll(entities)
    }

    @Transactional
    fun saveNewStudios(studios: List<Studio>): List<Studio> =
        studioRepository.saveAll(studios)

    fun findSyncProgress(taskName: String): SyncProgress? =
        syncProgressRepository.findByTaskName(taskName)

    @Transactional
    fun saveSyncProgress(
        existing: SyncProgress?,
        taskName: String,
        status: String,
        lastProcessedPage: Int? = null,
        totalPages: Int? = null,
        startedAt: LocalDateTime? = null,
        completedAt: LocalDateTime? = null,
    ): SyncProgress {
        val progress = SyncProgress(
            taskName = taskName,
            lastProcessedPage = lastProcessedPage ?: existing?.lastProcessedPage ?: 0,
            totalPages = totalPages ?: existing?.totalPages,
            status = status,
            startedAt = startedAt ?: existing?.startedAt,
            updatedAt = LocalDateTime.now(),
            completedAt = completedAt ?: existing?.completedAt,
            id = existing?.id ?: 0,
        )
        return syncProgressRepository.save(progress)
    }

    fun findAllGenres(): List<Genre> = genreRepository.findAll()

    fun findAllStudios(): List<Studio> = studioRepository.findAll()
}
