package movie.api

import movie.api.dto.MovieResponse
import movie.api.dto.MoviesResponse
import movie.api.dto.ScreeningResponse
import movie.domain.account.Account
import movie.domain.account.Point
import movie.domain.reservation.Seats
import movie.repository.ScreeningRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CinemaApiController(
    private val screeningRepository: ScreeningRepository,
    private val seats: Seats = Seats.create(),
    private val account: Account = Account(Point(0))
) {
    @GetMapping("/movies")
    fun getMovies(): MoviesResponse {
        val allScreenings = screeningRepository.findAll()
        val movieResponses = allScreenings.groupBy { it.movie }.map { (movie, screenings) ->
            MovieResponse(
                id = movie.id,
                title = movie.title.value,
                runningTimeMinutes = movie.runningTime.value,
                screenings = screenings.map {
                    ScreeningResponse(it.id, it.startTime.value, it.endTime())
                },
            )
        }
        return MoviesResponse(movieResponses)
    }
}
