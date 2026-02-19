package com.anirec.domain.rating.controller

import com.anirec.domain.auth.service.MemberService
import com.anirec.domain.rating.dto.RatingRequest
import com.anirec.domain.rating.dto.RatingResponse
import com.anirec.domain.rating.dto.RatingWithAnimeResponse
import com.anirec.domain.rating.service.RatingService
import com.anirec.global.security.SupabaseAuthentication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/ratings")
class RatingController(
    private val ratingService: RatingService,
    private val memberService: MemberService,
) {

    @PutMapping
    suspend fun upsertRating(
        principal: Principal,
        @RequestBody request: RatingRequest,
    ): RatingResponse {
        val auth = principal as SupabaseAuthentication
        memberService.syncFromToken(auth)
        return ratingService.upsertRating(auth.userId, request)
    }

    @GetMapping("/me")
    suspend fun getMyRatings(principal: Principal): List<RatingWithAnimeResponse> =
        ratingService.getMyRatings(principal.name)

    @GetMapping("/me/{malId}")
    suspend fun getRating(
        principal: Principal,
        @PathVariable malId: Long,
    ): ResponseEntity<RatingResponse> {
        val rating = ratingService.getRating(principal.name, malId)
        return if (rating != null) ResponseEntity.ok(rating)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{malId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteRating(principal: Principal, @PathVariable malId: Long) {
        ratingService.deleteRating(principal.name, malId)
    }
}
