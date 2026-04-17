package movie.domain.screening

import movie.constants.ErrorMessages
import movie.domain.reservation.Seat
import java.time.LocalDateTime

class Screening private constructor(
    val id: Long = 0L,
    val movie: Movie,
    val startTime: ScreeningStartTime,
    val reservedSeats: List<Seat>,
) {
    fun isSeatReserved(seat: Seat): Boolean = reservedSeats.contains(seat)

    fun isReserved(seats: List<Seat>) {
        require(seats.none { reservedSeats.contains(it) }) { ErrorMessages.SELECTED_RESERVED_SEAT.message }
    }

    fun reserve(seats: List<Seat>): Screening {
        require(seats.none { isSeatReserved(it) }) { ErrorMessages.SELECTED_RESERVED_SEAT.message }
        return Screening(
            id = this.id,
            movie = movie,
            startTime = startTime,
            reservedSeats = reservedSeats + seats,
        )
    }

    fun endTime(): LocalDateTime = startTime.value.plusMinutes(movie.runningTime.value.toLong())

    fun overlaps(otherScreen: Screening): Boolean = startTime.value < otherScreen.endTime() && otherScreen.startTime.value < endTime()

    companion object {
        fun create(
            id: Long = 0L,
            movie: Movie,
            startTime: ScreeningStartTime,
            reservedSeats: List<Seat> = emptyList(),
        ): Screening =
            Screening(
                id = id,
                movie = movie,
                startTime = startTime,
                reservedSeats = reservedSeats,
            )
    }
}

@JvmInline
value class ScreeningStartTime(
    val value: LocalDateTime,
)
