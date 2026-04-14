package domain.reservation

import domain.screening.Screening
import repository.Screenings

class Reservation(
    private val screenings: Screenings,
    private var cart: Cart,
) {
    fun addOneReservationScreen(screen: Screening, seats: List<Seat>): ReservedScreen {
        val newReservation = ReservedScreen(screen, seats)
        cart.add(newReservation)
        updateScreeningReservation(screen, newReservation.seats)
        return newReservation
    }

    fun updateScreeningReservation(screening: Screening, selectedSeats: List<Seat>) {
        screenings.updateScreening(
            this@Reservation.screenings.screenings.map {
                if(it.movie == screening.movie && it.startTime == screening.startTime) {
                    it.reserve(selectedSeats)
                } else {
                    it
                }
            }
        )
    }

    fun checkReservedSeat(inputSeatNumber: List<String>, allSeats: Seats, screening: Screening): List<Seat> {
        val selectedSeats = allSeats.findAllBySeatNumbers(inputSeatNumber)
        screening.isReserved(selectedSeats)
        return selectedSeats
    }

    fun checkScreeningOverlap(selectedScreening: Screening) =
        cart.checkScreeningOverlap(selectedScreening)


    fun findScreening(selectedNumber: Int, availableScreenings: List<Screening>) =
        screenings.findSelectedScreening(selectedNumber, availableScreenings)

    fun reserveResultCart() = cart
}