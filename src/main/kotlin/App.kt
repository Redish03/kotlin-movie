import controller.CinemaController
import controller.ScreeningMockData
import domain.reservation.Cart
import domain.reservation.Reservation
import repository.Screenings
import view.InputView
import view.OutputView

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
            reservation = Reservation(
                screenings = repository,
                cart = Cart(),
            )
        )

    controller.run()
}
