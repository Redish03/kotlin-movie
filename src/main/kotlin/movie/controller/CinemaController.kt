package movie.controller

import movie.domain.account.Account
import movie.domain.payment.PayResult
import movie.domain.payment.Payment
import movie.domain.payment.paymentmethod.PaymentMethod
import movie.domain.reservation.Reservation
import movie.domain.reservation.Seat
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import movie.repository.Screenings
import movie.view.InputView
import movie.view.OutputView
import java.time.LocalDate

class CinemaController(
    private val screenings: Screenings,
    private val reservation: Reservation,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val account: Account = Account(),
    private val allSeats: Seats = Seats.create(),
) {
    fun run() {
        if (!inputView.isReservationStarted()) {
            outputView.printEndTicketing()
            return
        }

        reserveMovies()
        proceedPayment()
    }

    private fun reserveMovies() {
        do {
            retryPrompt { reserveOneMovie() }
        } while (inputView.isAddMoreMovie())
    }

    private fun reserveOneMovie() {
        val foundedScreenings = retryPrompt { inputMovieInfo() }
        val selectedScreening = retryPrompt { readAvailableScreening(foundedScreenings) }
        val selectedSeats = retryPrompt { readAvailableSeats(selectedScreening) }

        outputView.printCartAdded(reservation.addOneReservationScreen(selectedScreening, selectedSeats))
    }

    private fun inputMovieInfo(): List<Screening> {
        val title = retryPrompt { inputView.readMovieTitle() }
        val date = retryPrompt { LocalDate.parse(inputView.readDate()) }

        return screenings.findByMovieTitleAndDate(title, date)
    }

    private fun readAvailableScreening(availableScreenings: List<Screening>): Screening =
        retryPrompt {
            outputView.printScreenings(availableScreenings)

            val selectedNumber = inputView.readScreeningNumber()
            val selectedScreening = reservation.findScreening(selectedNumber, availableScreenings)

            reservation.checkScreeningOverlap(selectedScreening)
            selectedScreening
        }

    private fun readAvailableSeats(screening: Screening): List<Seat> {
        outputView.printSeatLayout(allSeats, screening.reservedSeats)
        val inputSeat = retryPrompt { inputView.readSeatNumbers() }

        return reservation.checkReservedSeat(inputSeat, allSeats, screening)
    }

    private fun proceedPayment() {
        val result = reservation.reserveResultCart()
        outputView.printCart(result)

        val point = retryPrompt { inputView.readPointAmount() }
        val paymentMethod = retryPrompt { PaymentMethod.classifyPaymentMethod(inputView.readPaymentMethod()) }
        val payment = Payment(result)

        when (val result = payment.pay(point, account, paymentMethod)) {
            is PayResult.Success -> confirmPayment(result)
            is PayResult.Failure -> outputView.printErrorMessage(result.message)
        }
    }

    private fun confirmPayment(result: PayResult.Success) {
        outputView.printTotalCost(result.paidAmount)

        if (inputView.isConfirmPay()) {
            printReservationResult(result)
            return
        }

        outputView.printCancelPay()
    }

    private fun printReservationResult(result: PayResult.Success) {
        outputView.printFinishReservationMessage()

        result.cart.items.forEach {
            outputView.printTicketReservationInformation(it.seats, it.screen)
        }

        outputView.printPaymentResult(result.paidAmount, result.usedPoint)
    }

    private fun <T> retryPrompt(action: () -> T): T {
        while (true) {
            try {
                return action()
            } catch (e: Exception) {
                outputView.printErrorMessage(e.message ?: "")
            }
        }
    }
}
