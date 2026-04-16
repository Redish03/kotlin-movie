import controller.CinemaController
import controller.ScreeningMockData
import database.DatabaseConnection
import domain.reservation.Cart
import domain.reservation.Reservation
import repository.JdbcScreeningRepository
import repository.ScreeningRepository
import repository.Screenings
import view.InputView
import view.OutputView

fun main() {
    val connection = DatabaseConnection.getConnection()
    val dbRepository: ScreeningRepository = JdbcScreeningRepository(connection)
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
