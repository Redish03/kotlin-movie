package movie.domain.reservation

import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatGrade
import movie.domain.reservation.SeatRow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SeatTest {
    @Test
    fun `좌석은 행과 열로 구성된다`() {
        val row = SeatRow("A")
        val column = SeatColumn(1)
        val seat = Seat(row, column, SeatGrade.S)

        assertThat(seat.row).isEqualTo(row)
        assertThat(seat.column).isEqualTo(column)
        assertThat(seat.grade).isEqualTo(SeatGrade.S)
    }

    @Test
    fun `S 좌석은 18,000원이다`() {
        val row = SeatRow("A")
        val column = SeatColumn(1)
        val seat = Seat(row, column, SeatGrade.S)

        assertThat(seat.grade.money).isEqualTo(18_000)
    }

    @Test
    fun `A 좌석은 15,000원이다`() {
        val row = SeatRow("C")
        val column = SeatColumn(1)
        val seat = Seat(row, column, SeatGrade.A)

        assertThat(seat.grade.money).isEqualTo(15_000)
    }

    @Test
    fun `B 좌석은 15,000원이다`() {
        val row = SeatRow("E")
        val column = SeatColumn(1)
        val seat = Seat(row, column, SeatGrade.B)

        assertThat(seat.grade.money).isEqualTo(12_000)
    }

    @Test
    fun `좌석은 행과 열이 합쳐진 좌석 번호를 가진다`() {
        val row = SeatRow("A")
        val column = SeatColumn(1)
        val seat = Seat(row, column, SeatGrade.S)

        assertThat(seat.seatNumber).isEqualTo("A1")
    }
}
