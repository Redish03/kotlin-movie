package domain.reservation

import domain.screening.Screening

class ReservedScreen(
    val screen: Screening,
    val seats: List<Seat>,
) {
    fun price(): Int = seats.sumOf { it.grade.money }
}
