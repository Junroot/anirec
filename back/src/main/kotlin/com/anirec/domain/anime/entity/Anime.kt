package com.anirec.domain.anime.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(
    name = "animes",
    indexes = [
        Index(name = "idx_anime_score", columnList = "score"),
        Index(name = "idx_anime_popularity", columnList = "popularity"),
        Index(name = "idx_anime_rank", columnList = "`rank`"),
        Index(name = "idx_anime_year_season", columnList = "year, season"),
        Index(name = "idx_anime_status", columnList = "status"),
        Index(name = "idx_anime_type", columnList = "type"),
    ]
)
class Anime(
    @Column(unique = true, nullable = false)
    val malId: Long,

    @Column(nullable = false, length = 500)
    val title: String,

    @Column(length = 500)
    val titleJapanese: String? = null,

    @Column(columnDefinition = "TEXT")
    val synopsis: String? = null,

    val score: Double? = null,
    val scoredBy: Long? = null,
    @Column(name = "`rank`")
    val rank: Int? = null,
    val popularity: Int? = null,
    val members: Long? = null,
    val episodes: Int? = null,

    @Column(length = 50)
    val status: String? = null,

    @Column(length = 20)
    val type: String? = null,

    @Column(length = 20)
    val season: String? = null,

    val year: Int? = null,

    @Column(length = 500)
    val imageUrl: String? = null,

    @Column(length = 500)
    val largeImageUrl: String? = null,

    val airedFrom: LocalDate? = null,
    val airedTo: LocalDate? = null,

    @Column(length = 500)
    val url: String? = null,

    @ManyToMany
    @JoinTable(
        name = "anime_genre",
        joinColumns = [JoinColumn(name = "anime_id")],
        inverseJoinColumns = [JoinColumn(name = "genre_id")]
    )
    val genres: MutableSet<Genre> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "anime_studio",
        joinColumns = [JoinColumn(name = "anime_id")],
        inverseJoinColumns = [JoinColumn(name = "studio_id")]
    )
    val studios: MutableSet<Studio> = mutableSetOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
)
