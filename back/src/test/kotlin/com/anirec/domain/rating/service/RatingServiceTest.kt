package com.anirec.domain.rating.service

import com.anirec.domain.anime.entity.Anime
import com.anirec.domain.anime.repository.AnimeRepository
import com.anirec.domain.auth.entity.Member
import com.anirec.domain.auth.repository.MemberRepository
import com.anirec.domain.rating.dto.RatingRequest
import com.anirec.domain.rating.entity.Rating
import com.anirec.domain.rating.repository.RatingRepository
import com.anirec.global.exception.AnimeNotFoundException
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RatingServiceTest {

    private val ratingRepository: RatingRepository = mockk()
    private val animeRepository: AnimeRepository = mockk()
    private val memberRepository: MemberRepository = mockk()
    private val transactionTemplate: TransactionTemplate = mockk<TransactionTemplate>().also {
        every { it.execute(any<TransactionCallback<*>>()) } answers {
            val callback = firstArg<TransactionCallback<*>>()
            callback.doInTransaction(mockk())
        }
    }

    private val service = RatingService(ratingRepository, animeRepository, memberRepository, transactionTemplate)

    private val memberId = "user-001"
    private val member = Member(id = memberId, email = "test@test.com", username = "tester")

    private val anime = Anime(
        malId = 5114,
        title = "Fullmetal Alchemist: Brotherhood",
        score = 9.1,
        episodes = 64,
        status = "Finished Airing",
        type = "TV",
        imageUrl = "https://example.com/fma.jpg",
        id = 1,
    )

    private val anime2 = Anime(
        malId = 1535,
        title = "Death Note",
        score = 8.62,
        episodes = 37,
        status = "Finished Airing",
        type = "TV",
        imageUrl = "https://example.com/dn.jpg",
        id = 2,
    )

    @Nested
    inner class UpsertRating {

        @Test
        fun `creates new rating when none exists`() = runTest {
            val request = RatingRequest(animeId = 5114, score = 9, watchStatus = "Completed")
            val savedRating = Rating(
                member = member, anime = anime, score = 9, watchStatus = "Completed", id = 1,
            )

            every { animeRepository.findByMalId(5114) } returns anime
            every { memberRepository.getReferenceById(memberId) } returns member
            every { ratingRepository.findByMemberIdAndAnimeId(memberId, 1) } returns null
            every { ratingRepository.save(any()) } returns savedRating

            val result = service.upsertRating(memberId, request)

            assertEquals(5114, result.animeId)
            assertEquals(9, result.score)
            assertEquals("Completed", result.watchStatus)
            verify { ratingRepository.save(any()) }
        }

        @Test
        fun `updates existing rating`() = runTest {
            val request = RatingRequest(animeId = 5114, score = 10, watchStatus = "Completed")
            val existingRating = Rating(
                member = member, anime = anime, score = 8, watchStatus = "Watching", id = 1,
            )
            val ratingSlot = slot<Rating>()

            every { animeRepository.findByMalId(5114) } returns anime
            every { memberRepository.getReferenceById(memberId) } returns member
            every { ratingRepository.findByMemberIdAndAnimeId(memberId, 1) } returns existingRating
            every { ratingRepository.save(capture(ratingSlot)) } answers { ratingSlot.captured }

            val result = service.upsertRating(memberId, request)

            assertEquals(10, result.score)
            assertEquals("Completed", result.watchStatus)
            assertEquals(10, ratingSlot.captured.score)
            assertEquals("Completed", ratingSlot.captured.watchStatus)
        }

        @Test
        fun `throws AnimeNotFoundException when anime not found`() = runTest {
            val request = RatingRequest(animeId = 99999, score = 9, watchStatus = "Completed")

            every { animeRepository.findByMalId(99999) } returns null

            assertThrows<AnimeNotFoundException> {
                service.upsertRating(memberId, request)
            }
        }
    }

    @Nested
    inner class GetMyRatings {

        @Test
        fun `returns list of ratings with anime info`() = runTest {
            val ratings = listOf(
                Rating(member = member, anime = anime, score = 9, watchStatus = "Completed", id = 1),
                Rating(member = member, anime = anime2, score = 8, watchStatus = "Watching", id = 2),
            )

            every { ratingRepository.findAllByMemberIdOrderByUpdatedAtDesc(memberId) } returns ratings

            val result = service.getMyRatings(memberId)

            assertEquals(2, result.size)
            assertEquals("Fullmetal Alchemist: Brotherhood", result[0].animeTitle)
            assertEquals(5114, result[0].animeId)
            assertEquals("Death Note", result[1].animeTitle)
            assertEquals(1535, result[1].animeId)
        }

        @Test
        fun `returns empty list when no ratings`() = runTest {
            every { ratingRepository.findAllByMemberIdOrderByUpdatedAtDesc(memberId) } returns emptyList()

            val result = service.getMyRatings(memberId)

            assertEquals(0, result.size)
        }
    }

    @Nested
    inner class GetRating {

        @Test
        fun `returns rating when exists`() = runTest {
            val rating = Rating(member = member, anime = anime, score = 9, watchStatus = "Completed", id = 1)

            every { animeRepository.findByMalId(5114) } returns anime
            every { ratingRepository.findByMemberIdAndAnimeId(memberId, 1) } returns rating

            val result = service.getRating(memberId, 5114)

            assertNotNull(result)
            assertEquals(5114, result.animeId)
            assertEquals(9, result.score)
        }

        @Test
        fun `returns null when anime not found`() = runTest {
            every { animeRepository.findByMalId(99999) } returns null

            val result = service.getRating(memberId, 99999)

            assertNull(result)
        }

        @Test
        fun `returns null when rating not found`() = runTest {
            every { animeRepository.findByMalId(5114) } returns anime
            every { ratingRepository.findByMemberIdAndAnimeId(memberId, 1) } returns null

            val result = service.getRating(memberId, 5114)

            assertNull(result)
        }
    }

    @Nested
    inner class DeleteRating {

        @Test
        fun `deletes rating successfully`() = runTest {
            every { animeRepository.findByMalId(5114) } returns anime
            justRun { ratingRepository.deleteByMemberIdAndAnimeId(memberId, 1) }

            service.deleteRating(memberId, 5114)

            verify { ratingRepository.deleteByMemberIdAndAnimeId(memberId, 1) }
        }

        @Test
        fun `throws AnimeNotFoundException when anime not found`() = runTest {
            every { animeRepository.findByMalId(99999) } returns null

            assertThrows<AnimeNotFoundException> {
                service.deleteRating(memberId, 99999)
            }
        }
    }
}
