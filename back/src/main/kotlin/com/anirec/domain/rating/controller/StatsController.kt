package com.anirec.domain.rating.controller

import com.anirec.domain.rating.dto.StatsResponse
import com.anirec.domain.rating.service.StatsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/stats")
class StatsController(
    private val statsService: StatsService,
) {

    @GetMapping("/me")
    suspend fun getMyStats(principal: Principal): StatsResponse =
        statsService.getMyStats(principal.name)
}
