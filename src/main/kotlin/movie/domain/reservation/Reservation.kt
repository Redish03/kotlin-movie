package movie.domain.reservation

import movie.domain.screening.Screening
import movie.repository.ScreeningRepository

class Reservation(
    private val screenings: ScreeningRepository,
    private var cart: Cart,
) {
    fun addOneReservationScreen(
        screen: Screening,
        seats: List<Seat>,
    ): ReservedScreen {
        val newReservation = ReservedScreen(screen, seats)
        cart = cart.add(newReservation)
        updateScreeningReservation(screen, newReservation.seats)
        return newReservation
    }

    fun updateScreeningReservation(
        screening: Screening,
        selectedSeats: List<Seat>,
    ) {
        val updatedScreening = screening.reserve(selectedSeats)
        screenings.updateScreening(updatedScreening)
    }

    fun checkReservedSeat(
        inputSeatNumber: List<String>,
        allSeats: Seats,
        screening: Screening,
    ): List<Seat> {
        val selectedSeats = allSeats.findAllBySeatNumbers(inputSeatNumber)
        screening.isReserved(selectedSeats)
        return selectedSeats
    }

    fun checkScreeningOverlap(selectedScreening: Screening) = cart.checkScreeningOverlap(selectedScreening)

    fun findScreening(
        selectedNumber: Int,
        availableScreenings: List<Screening>,
    ) = screenings.findSelectedScreening(selectedNumber, availableScreenings)

    fun reserveResultCart() = cart
}
