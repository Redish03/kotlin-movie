package movie.domain.reservation

import movie.domain.screening.Screening

class ReservedScreen(
    val screen: Screening,
    val seats: List<Seat>,
) {
    fun price(): Int = seats.sumOf { it.grade.money }
}
