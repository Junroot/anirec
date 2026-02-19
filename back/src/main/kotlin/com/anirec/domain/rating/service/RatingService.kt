package com.anirec.domain.rating.service

import com.anirec.domain.anime.repository.AnimeRepository
import com.anirec.domain.auth.repository.MemberRepository
import com.anirec.domain.rating.dto.RatingRequest
import com.anirec.domain.rating.dto.RatingResponse
import com.anirec.domain.rating.dto.RatingWithAnimeResponse
import com.anirec.domain.rating.entity.Rating
import com.anirec.domain.rating.repository.RatingRepository
import com.anirec.global.exception.AnimeNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RatingService(
    private val ratingRepository: RatingRepository,
    private val animeRepository: AnimeRepository,
    private val memberRepository: MemberRepository,
) {

    @Transactional
    suspend fun upsertRating(memberId: String, request: RatingRequest): RatingResponse =
        withContext(Dispatchers.IO) {
            val anime = animeRepository.findByMalId(request.animeId)
                ?: throw AnimeNotFoundException("Anime not found: mal_id=${request.animeId}")
            val member = memberRepository.getReferenceById(memberId)

            val existing = ratingRepository.findByMemberIdAndAnimeId(memberId, anime.id)
            val rating = if (existing != null) {
                existing.score = request.score
                existing.watchStatus = request.watchStatus
                existing.updatedAt = LocalDateTime.now()
                ratingRepository.save(existing)
            } else {
                ratingRepository.save(
                    Rating(
                        member = member,
                        anime = anime,
                        score = request.score,
                        watchStatus = request.watchStatus,
                    )
                )
            }
            toResponse(rating)
        }

    suspend fun getMyRatings(memberId: String): List<RatingWithAnimeResponse> =
        withContext(Dispatchers.IO) {
            ratingRepository.findAllByMemberIdOrderByUpdatedAtDesc(memberId).map { toWithAnimeResponse(it) }
        }

    suspend fun getRating(memberId: String, malId: Long): RatingResponse? =
        withContext(Dispatchers.IO) {
            val anime = animeRepository.findByMalId(malId) ?: return@withContext null
            ratingRepository.findByMemberIdAndAnimeId(memberId, anime.id)?.let { toResponse(it) }
        }

    @Transactional
    suspend fun deleteRating(memberId: String, malId: Long) =
        withContext(Dispatchers.IO) {
            val anime = animeRepository.findByMalId(malId)
                ?: throw AnimeNotFoundException("Anime not found: mal_id=$malId")
            ratingRepository.deleteByMemberIdAndAnimeId(memberId, anime.id)
        }

    private fun toResponse(rating: Rating) = RatingResponse(
        id = rating.id,
        animeId = rating.anime.malId,
        score = rating.score,
        watchStatus = rating.watchStatus,
        createdAt = rating.createdAt,
        updatedAt = rating.updatedAt,
    )

    private fun toWithAnimeResponse(rating: Rating) = RatingWithAnimeResponse(
        id = rating.id,
        animeId = rating.anime.malId,
        animeTitle = rating.anime.title,
        animeImageUrl = rating.anime.imageUrl,
        animeType = rating.anime.type,
        animeEpisodes = rating.anime.episodes,
        score = rating.score,
        watchStatus = rating.watchStatus,
        createdAt = rating.createdAt,
        updatedAt = rating.updatedAt,
    )
}
