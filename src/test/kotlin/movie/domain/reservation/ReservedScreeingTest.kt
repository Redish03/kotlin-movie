package movie.domain.reservation

import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatGrade
import movie.domain.reservation.SeatRow
import movie.domain.screening.Movie
import movie.domain.screening.MovieTitle
import movie.domain.screening.RunningTime
import movie.domain.screening.Screening
import movie.domain.screening.ScreeningStartTime
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ReservedScreeingTest {
    @Test
    fun `예약된 좌석을 확인한다`() {
        val seats = listOf(Seat(SeatRow("A"), SeatColumn(1), SeatGrade.S))
        val reservedSeats =
            Screening.create(
                movie =
                    Movie(
                        title = MovieTitle("어벤져스"),
                        runningTime = RunningTime(120),
                    ),
                startTime = ScreeningStartTime(LocalDateTime.now()),
                reservedSeats = seats,
            )

        assertTrue(reservedSeats.isSeatReserved(Seat(SeatRow("A"), SeatColumn(1), SeatGrade.S)))
    }

    @Test
    fun `예약이 되어 있지 않다면 False를 반환한다`() {
        val seats = listOf(Seat(SeatRow("A"), SeatColumn(1), SeatGrade.S))
        val reservedSeats =
            Screening.create(
                movie =
                    Movie(
                        title = MovieTitle("어벤져스"),
                        runningTime = RunningTime(120),
                    ),
                startTime = ScreeningStartTime(LocalDateTime.now()),
                reservedSeats = seats,
            )

        assertTrue(!reservedSeats.isSeatReserved(Seat(SeatRow("B"), SeatColumn(1), SeatGrade.S)))
    }
}
