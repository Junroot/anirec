package com.anirec.domain.anime.repository

import com.anirec.domain.anime.entity.Anime
import com.anirec.domain.anime.entity.Genre
import com.anirec.domain.anime.entity.Studio
import com.anirec.global.config.QueryDslConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfig::class)
class AnimeRepositoryCustomImplTest {

    companion object {
        @Container
        @JvmStatic
        val mysql: MySQLContainer<*> = MySQLContainer("mysql:8.0")
            .withDatabaseName("anirec_test")
            .withUsername("test")
            .withPassword("test")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { mysql.jdbcUrl }
            registry.add("spring.datasource.username") { mysql.username }
            registry.add("spring.datasource.password") { mysql.password }
            registry.add("spring.datasource.driver-class-name") { mysql.driverClassName }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    lateinit var animeRepository: AnimeRepository

    @Autowired
    lateinit var entityManager: TestEntityManager

    private lateinit var genreAction: Genre
    private lateinit var genreComedy: Genre
    private lateinit var studioMappa: Studio
    private lateinit var studioUfotable: Studio

    @BeforeEach
    fun setUp() {
        // Clean up
        animeRepository.deleteAll()
        entityManager.flush()

        // Genres
        genreAction = entityManager.persist(Genre(malId = 1, name = "Action", count = 5000))
        genreComedy = entityManager.persist(Genre(malId = 4, name = "Comedy", count = 3000))

        // Studios
        studioMappa = entityManager.persist(Studio(malId = 569, name = "MAPPA"))
        studioUfotable = entityManager.persist(Studio(malId = 43, name = "ufotable"))

        entityManager.flush()

        // Anime 1: Jujutsu Kaisen - TV, Airing, Action, MAPPA
        val anime1 = Anime(
            malId = 40748,
            title = "Jujutsu Kaisen",
            score = 8.6,
            scoredBy = 1000000,
            rank = 50,
            popularity = 10,
            members = 2000000,
            episodes = 24,
            status = "Currently Airing",
            type = "TV",
            season = "fall",
            year = 2020,
            imageUrl = "https://example.com/jjk.jpg",
            largeImageUrl = "https://example.com/jjk_large.jpg",
            airedFrom = LocalDate.of(2020, 10, 3),
            airedTo = null,
            url = "https://myanimelist.net/anime/40748",
        )
        anime1.genres.add(genreAction)
        anime1.studios.add(studioMappa)
        entityManager.persist(anime1)

        // Anime 2: Demon Slayer - TV, Finished, Action, ufotable
        val anime2 = Anime(
            malId = 38000,
            title = "Demon Slayer",
            score = 8.5,
            scoredBy = 1500000,
            rank = 60,
            popularity = 5,
            members = 3000000,
            episodes = 26,
            status = "Finished Airing",
            type = "TV",
            season = "spring",
            year = 2019,
            imageUrl = "https://example.com/ds.jpg",
            largeImageUrl = "https://example.com/ds_large.jpg",
            airedFrom = LocalDate.of(2019, 4, 6),
            airedTo = LocalDate.of(2019, 9, 28),
            url = "https://myanimelist.net/anime/38000",
        )
        anime2.genres.add(genreAction)
        anime2.studios.add(studioUfotable)
        entityManager.persist(anime2)

        // Anime 3: Spy x Family - TV, Finished, Action+Comedy, MAPPA (no score for some tests)
        val anime3 = Anime(
            malId = 50265,
            title = "Spy x Family",
            score = null,
            scoredBy = null,
            rank = null,
            popularity = 3,
            members = 1500000,
            episodes = 12,
            status = "Finished Airing",
            type = "TV",
            season = "spring",
            year = 2022,
            imageUrl = "https://example.com/spy.jpg",
            largeImageUrl = "https://example.com/spy_large.jpg",
            airedFrom = LocalDate.of(2022, 4, 9),
            airedTo = LocalDate.of(2022, 6, 25),
            url = "https://myanimelist.net/anime/50265",
        )
        anime3.genres.add(genreAction)
        anime3.genres.add(genreComedy)
        anime3.studios.add(studioMappa)
        entityManager.persist(anime3)

        // Anime 4: Your Name - Movie, Finished, Comedy
        val anime4 = Anime(
            malId = 32281,
            title = "Kimi no Na wa.",
            score = 8.9,
            scoredBy = 2000000,
            rank = 20,
            popularity = 8,
            members = 2500000,
            episodes = 1,
            status = "Finished Airing",
            type = "Movie",
            season = null,
            year = 2016,
            imageUrl = "https://example.com/knw.jpg",
            largeImageUrl = "https://example.com/knw_large.jpg",
            airedFrom = LocalDate.of(2016, 8, 26),
            airedTo = LocalDate.of(2016, 8, 26),
            url = "https://myanimelist.net/anime/32281",
        )
        anime4.genres.add(genreComedy)
        entityManager.persist(anime4)

        // Anime 5: Upcoming anime - TV, Not yet aired
        val anime5 = Anime(
            malId = 99999,
            title = "Future Anime",
            score = null,
            scoredBy = null,
            rank = null,
            popularity = 100,
            members = 50000,
            episodes = null,
            status = "Not yet aired",
            type = "TV",
            season = "winter",
            year = 2026,
            airedFrom = null,
            airedTo = null,
            url = "https://myanimelist.net/anime/99999",
        )
        entityManager.persist(anime5)

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    @DisplayName("필터 없이 검색하면 전체 결과를 반환한다")
    fun searchWithoutFilters() {
        val page = animeRepository.search(pageable = PageRequest.of(0, 25))
        assertEquals(5, page.totalElements)
        assertEquals(5, page.content.size)
    }

    @Test
    @DisplayName("type 필터로 TV 애니메이션만 반환한다")
    fun searchByType() {
        val page = animeRepository.search(type = "TV", pageable = PageRequest.of(0, 25))
        assertEquals(4, page.totalElements)
        page.content.forEach { assertEquals("TV", it.type) }
    }

    @Test
    @DisplayName("type 필터는 대소문자를 무시한다")
    fun searchByTypeCaseInsensitive() {
        val page = animeRepository.search(type = "tv", pageable = PageRequest.of(0, 25))
        assertEquals(4, page.totalElements)
    }

    @Test
    @DisplayName("status 필터 'airing'이 'Currently Airing'으로 매핑된다")
    fun searchByStatusAiring() {
        val page = animeRepository.search(status = "airing", pageable = PageRequest.of(0, 25))
        assertEquals(1, page.totalElements)
        assertEquals("Currently Airing", page.content[0].status)
    }

    @Test
    @DisplayName("status 필터 'complete'이 'Finished Airing'으로 매핑된다")
    fun searchByStatusComplete() {
        val page = animeRepository.search(status = "complete", pageable = PageRequest.of(0, 25))
        assertEquals(3, page.totalElements)
        page.content.forEach { assertEquals("Finished Airing", it.status) }
    }

    @Test
    @DisplayName("status 필터 'upcoming'이 'Not yet aired'로 매핑된다")
    fun searchByStatusUpcoming() {
        val page = animeRepository.search(status = "upcoming", pageable = PageRequest.of(0, 25))
        assertEquals(1, page.totalElements)
        assertEquals("Not yet aired", page.content[0].status)
    }

    @Test
    @DisplayName("genre malIds로 필터링한다")
    fun searchByGenreMalIds() {
        val page = animeRepository.search(genreMalIds = listOf(4), pageable = PageRequest.of(0, 25))
        assertEquals(2, page.totalElements)
        // Comedy: Spy x Family, Your Name
        val titles = page.content.map { it.title }.toSet()
        assertTrue(titles.contains("Spy x Family"))
        assertTrue(titles.contains("Kimi no Na wa."))
    }

    @Test
    @DisplayName("producer(studio) malIds로 필터링한다")
    fun searchByProducerMalIds() {
        val page = animeRepository.search(producerMalIds = listOf(569), pageable = PageRequest.of(0, 25))
        assertEquals(2, page.totalElements)
        // MAPPA: Jujutsu Kaisen, Spy x Family
        val titles = page.content.map { it.title }.toSet()
        assertTrue(titles.contains("Jujutsu Kaisen"))
        assertTrue(titles.contains("Spy x Family"))
    }

    @Test
    @DisplayName("orderBy=score, sort=desc로 정렬한다")
    fun searchOrderByScoreDesc() {
        val page = animeRepository.search(orderBy = "score", sort = "desc", pageable = PageRequest.of(0, 25))
        val scores = page.content.mapNotNull { it.score }
        // score가 있는 항목은 내림차순, null은 뒤로
        for (i in 0 until scores.size - 1) {
            assertTrue(scores[i] >= scores[i + 1])
        }
        // null score 항목은 마지막에
        val lastItems = page.content.takeLast(2)
        assertTrue(lastItems.any { it.score == null })
    }

    @Test
    @DisplayName("orderBy=start_date, sort=desc로 정렬한다")
    fun searchOrderByStartDateDesc() {
        val page = animeRepository.search(orderBy = "start_date", sort = "desc", pageable = PageRequest.of(0, 25))
        val dates = page.content.mapNotNull { it.airedFrom }
        for (i in 0 until dates.size - 1) {
            assertTrue(dates[i] >= dates[i + 1])
        }
    }

    @Test
    @DisplayName("findTop은 score가 있는 항목만 score DESC로 반환한다")
    fun findTop() {
        val page = animeRepository.findTop(PageRequest.of(0, 25))
        // score == null인 Spy x Family, Future Anime은 제외
        assertEquals(3, page.totalElements)
        page.content.forEach { assertTrue(it.score != null) }
        val scores = page.content.map { it.score!! }
        for (i in 0 until scores.size - 1) {
            assertTrue(scores[i] >= scores[i + 1])
        }
    }

    @Test
    @DisplayName("findBySeason은 year + season으로 필터링한다")
    fun findBySeason() {
        val page = animeRepository.findBySeason(2020, "fall", PageRequest.of(0, 25))
        assertEquals(1, page.totalElements)
        assertEquals("Jujutsu Kaisen", page.content[0].title)
    }

    @Test
    @DisplayName("findBySeason은 season 대소문자를 무시한다")
    fun findBySeasonCaseInsensitive() {
        val page = animeRepository.findBySeason(2019, "SPRING", PageRequest.of(0, 25))
        assertEquals(1, page.totalElements)
        assertEquals("Demon Slayer", page.content[0].title)
    }

    @Test
    @DisplayName("페이지네이션이 올바르게 동작한다")
    fun pagination() {
        // page size 2, first page
        val page1 = animeRepository.search(pageable = PageRequest.of(0, 2))
        assertEquals(5, page1.totalElements)
        assertEquals(2, page1.content.size)
        assertTrue(page1.hasNext())
        assertEquals(3, page1.totalPages)

        // second page
        val page2 = animeRepository.search(pageable = PageRequest.of(1, 2))
        assertEquals(2, page2.content.size)
        assertTrue(page2.hasNext())

        // last page
        val page3 = animeRepository.search(pageable = PageRequest.of(2, 2))
        assertEquals(1, page3.content.size)
        assertFalse(page3.hasNext())
    }

    @Test
    @DisplayName("복합 필터(type + genres + orderBy)가 동시에 적용된다")
    fun searchCombinedFilters() {
        // TV + Action genre + score desc
        val page = animeRepository.search(
            type = "TV",
            genreMalIds = listOf(1),
            orderBy = "score",
            sort = "desc",
            pageable = PageRequest.of(0, 25),
        )
        // TV + Action: Jujutsu Kaisen(8.6), Demon Slayer(8.5), Spy x Family(null)
        assertEquals(3, page.totalElements)
        assertEquals("Jujutsu Kaisen", page.content[0].title)
        assertEquals("Demon Slayer", page.content[1].title)
        assertEquals("Spy x Family", page.content[2].title)
    }

    @Test
    @DisplayName("fetch join으로 genres와 studios가 로드된다")
    fun fetchJoinLoadsRelations() {
        val page = animeRepository.search(
            type = "TV",
            genreMalIds = listOf(1, 4),
            pageable = PageRequest.of(0, 25),
        )
        // Spy x Family has both Action and Comedy
        val spy = page.content.find { it.title == "Spy x Family" }!!
        assertEquals(2, spy.genres.size)
        assertEquals(1, spy.studios.size)
    }
}
