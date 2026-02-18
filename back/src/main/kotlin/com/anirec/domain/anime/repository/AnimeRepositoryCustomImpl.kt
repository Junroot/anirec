package com.anirec.domain.anime.repository

import com.anirec.domain.anime.entity.Anime
import com.anirec.domain.anime.entity.QAnime.anime
import com.anirec.domain.anime.entity.QGenre.genre
import com.anirec.domain.anime.entity.QStudio.studio
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class AnimeRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : AnimeRepositoryCustom {

    override fun search(
        type: String?,
        status: String?,
        genreMalIds: List<Long>?,
        producerMalIds: List<Long>?,
        orderBy: String?,
        sort: String?,
        pageable: Pageable,
    ): Page<Anime> {
        val where = BooleanBuilder()

        type?.let { where.and(anime.type.equalsIgnoreCase(it)) }
        status?.let { where.and(anime.status.eq(mapStatus(it))) }
        genreMalIds?.takeIf { it.isNotEmpty() }?.let { where.and(anime.genres.any().malId.`in`(it)) }
        producerMalIds?.takeIf { it.isNotEmpty() }?.let { where.and(anime.studios.any().malId.`in`(it)) }

        val order = buildOrderSpecifier(orderBy, sort)

        // Step 1: ID-only query with pagination
        val ids = queryFactory
            .select(anime.id)
            .from(anime)
            .where(where)
            .orderBy(order)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        if (ids.isEmpty()) {
            return PageableExecutionUtils.getPage(emptyList(), pageable) { 0L }
        }

        // Step 2: Fetch full entities with join fetch by IDs
        val content = queryFactory
            .selectFrom(anime)
            .leftJoin(anime.genres, genre).fetchJoin()
            .leftJoin(anime.studios, studio).fetchJoin()
            .where(anime.id.`in`(ids))
            .orderBy(order)
            .fetch()

        // Count query (lazy)
        val countQuery = queryFactory
            .select(anime.count())
            .from(anime)
            .where(where)

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    override fun findTop(pageable: Pageable): Page<Anime> {
        val where = anime.score.isNotNull

        val ids = queryFactory
            .select(anime.id)
            .from(anime)
            .where(where)
            .orderBy(anime.score.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        if (ids.isEmpty()) {
            return PageableExecutionUtils.getPage(emptyList(), pageable) { 0L }
        }

        val content = queryFactory
            .selectFrom(anime)
            .leftJoin(anime.genres, genre).fetchJoin()
            .leftJoin(anime.studios, studio).fetchJoin()
            .where(anime.id.`in`(ids))
            .orderBy(anime.score.desc())
            .fetch()

        val countQuery = queryFactory
            .select(anime.count())
            .from(anime)
            .where(where)

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    override fun findBySeason(year: Int, season: String, pageable: Pageable): Page<Anime> {
        val where = anime.year.eq(year).and(anime.season.equalsIgnoreCase(season))

        val ids = queryFactory
            .select(anime.id)
            .from(anime)
            .where(where)
            .orderBy(anime.score.desc().nullsLast())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        if (ids.isEmpty()) {
            return PageableExecutionUtils.getPage(emptyList(), pageable) { 0L }
        }

        val content = queryFactory
            .selectFrom(anime)
            .leftJoin(anime.genres, genre).fetchJoin()
            .leftJoin(anime.studios, studio).fetchJoin()
            .where(anime.id.`in`(ids))
            .orderBy(anime.score.desc().nullsLast())
            .fetch()

        val countQuery = queryFactory
            .select(anime.count())
            .from(anime)
            .where(where)

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun buildOrderSpecifier(orderBy: String?, sort: String?): OrderSpecifier<*> {
        val isAsc = sort?.lowercase() == "asc"

        return when (orderBy?.lowercase()) {
            "score" -> if (isAsc) anime.score.asc().nullsLast() else anime.score.desc().nullsLast()
            "title" -> if (isAsc) anime.title.asc() else anime.title.desc()
            "popularity" -> if (isAsc) anime.popularity.asc().nullsLast() else anime.popularity.desc().nullsLast()
            "start_date" -> if (isAsc) anime.airedFrom.asc().nullsLast() else anime.airedFrom.desc().nullsLast()
            else -> anime.malId.asc()
        }
    }

    companion object {
        private val STATUS_MAP = mapOf(
            "airing" to "Currently Airing",
            "complete" to "Finished Airing",
            "upcoming" to "Not yet aired",
        )

        fun mapStatus(status: String): String =
            STATUS_MAP[status.lowercase()] ?: status
    }
}
