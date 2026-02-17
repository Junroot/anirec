package com.anirec.domain.anime.controller

import com.anirec.domain.anime.dto.JikanResponse
import com.anirec.domain.anime.service.AnimeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/anime")
class AnimeController(private val animeService: AnimeService) {

    @GetMapping
    suspend fun search(
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) genres: String?,
        @RequestParam(required = false) orderBy: String?,
        @RequestParam(required = false) sort: String?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) limit: Int?,
    ): JikanResponse =
        animeService.search(query = q, page = page, limit = limit, type = type, genres = genres, orderBy = orderBy, sort = sort)

    @GetMapping("/top")
    suspend fun getTop(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) limit: Int?,
    ): JikanResponse =
        animeService.getTop(page = page, limit = limit)

    @GetMapping("/season")
    suspend fun getSeasonal(
        @RequestParam year: Int,
        @RequestParam season: String,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) limit: Int?,
    ): JikanResponse =
        animeService.getSeasonal(year = year, season = season, page = page, limit = limit)

    @GetMapping("/season/now")
    suspend fun getCurrentSeason(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) limit: Int?,
    ): JikanResponse =
        animeService.getCurrentSeason(page = page, limit = limit)
}
