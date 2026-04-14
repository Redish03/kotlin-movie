package controller

import domain.account.Account
import domain.payment.PayResult
import domain.payment.Payment
import domain.payment.PaymentMethod
import domain.reservation.Cart
import domain.reservation.ReservedScreen
import domain.reservation.Seat
import domain.reservation.Seats
import domain.screening.Screening
import repository.Screenings
import view.InputView
import view.OutputView
import java.time.LocalDate

class CinemaController(
    private val screenings: Screenings,
    private val inputView: InputView,
    private val outputView: OutputView,
    private val account: Account = Account(),
    private val allSeats: Seats = Seats.create(),
) {
    private var cart: Cart = Cart()

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

        val reservedItem =
            ReservedScreen(
                screen = selectedScreening,
                seats = selectedSeats,
            )

        addToCart(reservedItem)
        outputView.printCartAdded(reservedItem)
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
            val selectedScreening =
                screenings.findSelectedScreening(selectedNumber, availableScreenings)

            cart.checkScreeningOverlap(selectedScreening)
            selectedScreening
        }

    private fun readAvailableSeats(screening: Screening): List<Seat> {
        outputView.printSeatLayout(allSeats, screening.reservedSeats)

        val inputSeat = retryPrompt { inputView.readSeatNumbers() }
        val selectedSeats = allSeats.findAllBySeatNumbers(inputSeat)
        screening.isReserved(selectedSeats)

        return selectedSeats
    }

    private fun addToCart(reservedItem: ReservedScreen) {
        cart = cart.add(reservedItem)
        updateScreeningReservation(reservedItem.screen, reservedItem.seats)
    }

    private fun proceedPayment() {
        outputView.printCart(cart)

        val point = retryPrompt { inputView.readPointAmount() }
        val paymentMethod = retryPrompt { PaymentMethod.validate(inputView.readPaymentMethod()) }
        val payment = Payment(cart)

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

    private fun updateScreeningReservation(
        screening: Screening,
        selectedSeats: List<Seat>,
    ) {
        screenings.updateScreening(
            this@CinemaController.screenings.screenings.map {
                if (it.movie == screening.movie && it.startTime == screening.startTime) {
                    it.reserve(selectedSeats)
                } else {
                    it
                }
            },
        )
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
