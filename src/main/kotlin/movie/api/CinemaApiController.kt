package movie.api

import movie.api.dto.MovieResponse
import movie.api.dto.MoviesResponse
import movie.api.dto.ReservationRequest
import movie.api.dto.ReservationResponse
import movie.api.dto.ScreeningResponse
import movie.domain.account.Account
import movie.domain.account.Point
import movie.domain.payment.PayResult
import movie.domain.payment.Payment
import movie.domain.payment.paymentmethod.Cash
import movie.domain.payment.paymentmethod.CreditCard
import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seats
import movie.repository.ScreeningRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
class CinemaApiController(
    private val screeningRepository: ScreeningRepository,
) {
    private val allSeats = Seats.create()
    private val account = Account(Point(10000))

    @GetMapping("/movies")
    fun getMovies(): MoviesResponse {
        val allScreenings = screeningRepository.findAll()
        val movieResponses =
            allScreenings
                .groupBy { it.movie.title.value }
                .map { (_, group) ->
                    val movie = group.first().movie
                    MovieResponse(
                        id = movie.id,
                        title = movie.title.value,
                        runningTimeMinutes = movie.runningTime.value,
                        screenings =
                            group
                                .map {
                                    ScreeningResponse(it.id, it.startTime.value, it.endTime())
                                }.sortedBy { it.startAt },
                    )
                }.sortedBy { it.title }

        return MoviesResponse(movieResponses)
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    fun reserve(
        @RequestBody request: ReservationRequest,
    ): ReservationResponse =
        try {
            var cart = Cart()

            request.reservations.forEach { item ->
                val screening =
                    screeningRepository.findById(item.screeningId)
                        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "상영 정보를 찾을 수 없습니다: ${item.screeningId}")

                val selectedSeats = allSeats.findAllBySeatNumbers(item.seats)
                val updatedScreening = screening.reserve(selectedSeats)

                screeningRepository.updateScreening(updatedScreening)
                cart = cart.add(ReservedScreen(updatedScreening, selectedSeats))
            }

            val paymentMethod =
                when (request.paymentMethod) {
                    "CREDIT_CARD" -> CreditCard()
                    "CASH" -> Cash()
                    else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 결제 수단입니다.")
                }

            val payment = Payment(cart)
            val result =
                payment.pay(
                    pointAmount = request.usedPoints,
                    account = account,
                    selectedPaymentMethod = paymentMethod,
                )

            when (result) {
                is PayResult.Success ->
                    ReservationResponse(
                        reservationId = 1L,
                        reservations = request.reservations,
                        usedPoints = result.usedPoint,
                        paymentMethod = request.paymentMethod,
                        totalPrice = result.paidAmount,
                    )

                is PayResult.Failure -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, result.message)
            }
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
}
