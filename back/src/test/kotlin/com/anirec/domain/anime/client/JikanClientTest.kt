package com.anirec.domain.anime.client

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JikanClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var jikanClient: JikanClient

    private val sampleResponse = """
        {
          "pagination": {
            "last_visible_page": 1,
            "has_next_page": false,
            "current_page": 1,
            "items": { "count": 1, "total": 1, "per_page": 25 }
          },
          "data": [
            {
              "mal_id": 1,
              "title": "Cowboy Bebop",
              "title_japanese": "カウボーイビバップ",
              "synopsis": "A space bounty hunter crew.",
              "score": 8.78,
              "scored_by": 900000,
              "rank": 28,
              "popularity": 39,
              "members": 1800000,
              "episodes": 26,
              "status": "Finished Airing",
              "type": "TV",
              "season": "spring",
              "year": 1998,
              "genres": [
                { "mal_id": 1, "name": "Action" },
                { "mal_id": 24, "name": "Sci-Fi" }
              ],
              "studios": [
                { "mal_id": 14, "name": "Sunrise" }
              ],
              "images": {
                "jpg": {
                  "image_url": "https://cdn.myanimelist.net/images/anime/4/19644.jpg",
                  "large_image_url": "https://cdn.myanimelist.net/images/anime/4/19644l.jpg"
                }
              },
              "aired": {
                "from": "1998-04-03T00:00:00+00:00",
                "to": "1999-04-24T00:00:00+00:00"
              },
              "url": "https://myanimelist.net/anime/1/Cowboy_Bebop"
            }
          ]
        }
    """.trimIndent()

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()
        jikanClient = JikanClient(webClient)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `searchAnime calls GET anime with query params`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(sampleResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )

        val result = jikanClient.searchAnime(page = 1, limit = 10, type = "tv", status = "complete", producers = "569")

        assertEquals(1, result.data.size)
        val anime = result.data[0]
        assertEquals(1L, anime.malId)
        assertEquals("Cowboy Bebop", anime.title)
        assertEquals("カウボーイビバップ", anime.titleJapanese)
        assertEquals(8.78, anime.score)
        assertEquals(26, anime.episodes)
        assertEquals(2, anime.genres?.size)
        assertEquals("Action", anime.genres?.get(0)?.name)
        assertEquals("Sunrise", anime.studios?.get(0)?.name)
        assertNotNull(anime.images?.jpg?.largeImageUrl)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        val path = request.path!!
        assertTrue(path.startsWith("/anime?"))
        assertTrue(path.contains("page=1"))
        assertTrue(path.contains("limit=10"))
        assertTrue(path.contains("type=tv"))
        assertTrue(path.contains("status=complete"))
        assertTrue(path.contains("producers=569"))
    }

    @Test
    fun `searchAnime excludes null params`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(sampleResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )

        jikanClient.searchAnime(status = "airing")

        val request = mockWebServer.takeRequest()
        val path = request.path!!
        assertTrue(path.contains("status=airing"))
        assertTrue(!path.contains("q="))
        assertTrue(!path.contains("page="))
        assertTrue(!path.contains("limit="))
        assertTrue(!path.contains("type="))
        assertTrue(!path.contains("producers="))
    }

    @Test
    fun `getTopAnime calls GET top anime`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(sampleResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )

        val result = jikanClient.getTopAnime(page = 1, limit = 25, filter = "airing")

        assertEquals(1, result.data.size)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        val path = request.path!!
        assertTrue(path.startsWith("/top/anime?"))
        assertTrue(path.contains("page=1"))
        assertTrue(path.contains("limit=25"))
        assertTrue(path.contains("filter=airing"))
    }

    @Test
    fun `getSeasonalAnime calls GET seasons year season`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(sampleResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )

        val result = jikanClient.getSeasonalAnime(year = 2024, season = "winter", page = 1)

        assertEquals(1, result.data.size)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        val path = request.path!!
        assertTrue(path.startsWith("/seasons/2024/winter"))
        assertTrue(path.contains("page=1"))
    }

    private val producerResponse = """
        {
          "pagination": {
            "last_visible_page": 1,
            "has_next_page": false,
            "current_page": 1,
            "items": { "count": 2, "total": 2, "per_page": 10 }
          },
          "data": [
            {
              "mal_id": 569,
              "titles": [
                { "type": "Default", "title": "MAPPA" }
              ],
              "count": 150
            },
            {
              "mal_id": 11,
              "titles": [
                { "type": "Default", "title": "Madhouse" },
                { "type": "Japanese", "title": "マッドハウス" }
              ],
              "count": 350
            }
          ]
        }
    """.trimIndent()

    @Test
    fun `searchProducers calls GET producers with query params`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(producerResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )

        val result = jikanClient.searchProducers(q = "mappa", page = 1, limit = 10)

        assertEquals(2, result.data.size)
        assertEquals(569L, result.data[0].malId)
        assertEquals("MAPPA", result.data[0].titles?.first()?.title)
        assertEquals(11L, result.data[1].malId)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        val path = request.path!!
        assertTrue(path.startsWith("/producers?"))
        assertTrue(path.contains("q=mappa"))
        assertTrue(path.contains("page=1"))
        assertTrue(path.contains("limit=10"))
        assertTrue(path.contains("order_by=count"))
        assertTrue(path.contains("sort=desc"))
    }

    @Test
    fun `getCurrentSeasonAnime calls GET seasons now`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(sampleResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        )

        val result = jikanClient.getCurrentSeasonAnime(page = 1, limit = 10)

        assertEquals(1, result.data.size)
        assertNotNull(result.pagination)
        assertEquals(1, result.pagination?.currentPage)
        assertEquals(false, result.pagination?.hasNextPage)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        val path = request.path!!
        assertTrue(path.startsWith("/seasons/now"))
        assertTrue(path.contains("page=1"))
        assertTrue(path.contains("limit=10"))
    }
}
