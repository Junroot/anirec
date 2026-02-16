package com.anirec.domain.auth.service

import com.anirec.domain.auth.entity.Member
import com.anirec.domain.auth.repository.MemberRepository
import com.anirec.global.security.SupabaseAuthentication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class MemberService(private val memberRepository: MemberRepository) {

    suspend fun syncFromToken(auth: SupabaseAuthentication): Member =
        withContext(Dispatchers.IO) {
            memberRepository.findById(auth.userId).orElseGet {
                memberRepository.save(
                    Member(
                        id = auth.userId,
                        email = auth.email,
                        username = auth.username,
                    )
                )
            }
        }

    suspend fun findById(id: String): Member? =
        withContext(Dispatchers.IO) {
            memberRepository.findById(id).orElse(null)
        }
}
