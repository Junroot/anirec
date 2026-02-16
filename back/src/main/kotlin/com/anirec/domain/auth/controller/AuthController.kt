package com.anirec.domain.auth.controller

import com.anirec.domain.auth.dto.MemberResponse
import com.anirec.domain.auth.service.MemberService
import com.anirec.global.security.SupabaseAuthentication
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val memberService: MemberService) {

    @GetMapping("/me")
    suspend fun me(): MemberResponse {
        val auth = ReactiveSecurityContextHolder.getContext()
            .awaitSingle().authentication as SupabaseAuthentication
        val member = memberService.syncFromToken(auth)
        return MemberResponse(
            id = member.id,
            email = member.email,
            username = member.username,
            createdAt = member.createdAt,
        )
    }
}
