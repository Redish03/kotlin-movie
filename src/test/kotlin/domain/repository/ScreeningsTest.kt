package domain.repository

import controller.ScreeningMockData
import domain.screening.Screening
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import repository.Screenings
import java.time.LocalDate

class ScreeningsTest {
    val screenings = Screenings(ScreeningMockData.screenings())

    @Test
    fun `제목과 날짜가 주어지면 해당하는 상영을 찾는다`() {
        val resultScreening =
            screenings.findByMovieTitleAndDate(
                "어벤져스",
                LocalDate.of(2026, 4, 10),
            )

        assertEquals(
            ScreeningMockData
                .screenings()
                .first()
                .movie.title.value,
            resultScreening
                .first()
                .movie.title.value,
        )
        assertEquals(
            ScreeningMockData
                .screenings()
                .first()
                .startTime.value,
            resultScreening.first().startTime.value,
        )
    }

    @Test
    fun `해당하는 상영이 없다면 빈 리스트가 반환된다`() {
        val title = "어벤져스"
        val time = LocalDate.of(2026, 4, 15)

        assertThrows<IllegalArgumentException> { screenings.findByMovieTitleAndDate(title, time) }
    }
}
