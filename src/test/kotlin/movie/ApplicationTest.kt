package movie

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest(
    @param:LocalServerPort private val port: Int,
) {
    private lateinit var client: RestTestClient

    @BeforeEach
    fun setUp() {
        client = RestTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `영화 목록을 조회한다`() {
        client.get().uri("/api/movies")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.movies").isArray()
    }

    @Test
    fun `예매를 생성한다`() {
        client.post().uri("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                """
                {
                  "reservations": [
                    {
                      "screeningId": 101,
                      "seats": ["C2", "C3"]
                    }
                  ],
                  "usedPoints": 2000,
                  "paymentMethod": "CREDIT_CARD"
                }
                """.trimIndent()
            )
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.reservationId").exists()
            .jsonPath("$.totalPrice").exists()
    }
}
