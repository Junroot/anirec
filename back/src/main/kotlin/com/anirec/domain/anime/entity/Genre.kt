package com.anirec.domain.anime.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "genres")
class Genre(
    @Column(unique = true, nullable = false)
    val malId: Long,

    @Column(nullable = false, length = 100)
    val name: String,

    val count: Int? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
)
