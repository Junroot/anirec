package com.anirec.domain.anime.repository

import com.anirec.domain.anime.entity.Anime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AnimeRepositoryCustom {

    fun search(
        type: String? = null,
        status: String? = null,
        genreMalIds: List<Long>? = null,
        producerMalIds: List<Long>? = null,
        orderBy: String? = null,
        sort: String? = null,
        year: Int? = null,
        season: String? = null,
        pageable: Pageable,
    ): Page<Anime>

    fun findTop(pageable: Pageable): Page<Anime>

    fun findBySeason(year: Int, season: String, pageable: Pageable): Page<Anime>
}
