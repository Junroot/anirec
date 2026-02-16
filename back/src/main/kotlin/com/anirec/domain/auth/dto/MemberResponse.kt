package com.anirec.domain.auth.dto

import java.time.LocalDateTime

data class MemberResponse(
    val id: String,
    val email: String,
    val username: String,
    val createdAt: LocalDateTime,
)
