package com.anirec.domain.auth.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "members")
class Member(
    @Id
    @Column(length = 36)
    val id: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false, length = 50)
    var username: String,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
