package movie

import movie.controller.CinemaController
import movie.controller.ScreeningMockData
import movie.domain.reservation.Cart
import movie.domain.reservation.Reservation
import movie.repository.Screenings
import movie.view.InputView
import movie.view.OutputView

fun main() {
    val repository =
        Screenings(
            screenings = ScreeningMockData.screenings(),
        )
    val controller =
        CinemaController(
            screenings = repository,
            inputView = InputView(),
            outputView = OutputView(),
            reservation =
                Reservation(
                    screenings = repository,
                    cart = Cart(),
                ),
        )

    controller.run()
}
