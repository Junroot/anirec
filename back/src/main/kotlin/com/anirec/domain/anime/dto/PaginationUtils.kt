package com.anirec.domain.anime.dto

import com.anirec.domain.anime.entity.Anime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

object PaginationUtils {

    private const val DEFAULT_PAGE = 1
    private const val DEFAULT_LIMIT = 25

    fun toPageable(page: Int?, limit: Int?): PageRequest {
        val pageNumber = (page ?: DEFAULT_PAGE).coerceAtLeast(1) - 1 // 1-based → 0-based
        val pageSize = (limit ?: DEFAULT_LIMIT).coerceIn(1, 25)
        return PageRequest.of(pageNumber, pageSize)
    }

    fun toJikanResponse(page: Page<Anime>): JikanResponse {
        val pagination = JikanResponse.Pagination(
            lastVisiblePage = page.totalPages.coerceAtLeast(1),
            hasNextPage = page.hasNext(),
            currentPage = page.number + 1, // 0-based → 1-based
            items = JikanResponse.PaginationItems(
                count = page.numberOfElements,
                total = page.totalElements.toInt(),
                perPage = page.size,
            ),
        )
        val data = page.content.map(AnimeMapper::toDto)
        return JikanResponse(pagination = pagination, data = data)
    }
}
