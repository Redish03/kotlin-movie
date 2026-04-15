package controller

import domain.screening.Movie
import domain.screening.MovieTitle
import domain.screening.RunningTime
import domain.screening.Screening
import domain.screening.ScreeningStartTime
import java.time.LocalDateTime

object ScreeningMockData {
    fun screenings(): List<Screening> {
        val movie1 =
            Movie(
                title = MovieTitle("어벤져스"),
                runningTime = RunningTime(120),
            )

        val movie2 =
            Movie(
                title = MovieTitle("인셉션"),
                runningTime = RunningTime(148),
            )

        val movie3 =
            Movie(
                title = MovieTitle("인터스텔라"),
                runningTime = RunningTime(169),
            )

        return listOf(
            Screening.create(
                movie = movie1,
                startTime =
                    ScreeningStartTime(
                        LocalDateTime.of(2026, 4, 10, 10, 0),
                    ),
            ),
            Screening.create(
                movie = movie1,
                startTime =
                    ScreeningStartTime(
                        LocalDateTime.of(2026, 4, 10, 14, 0),
                    ),
            ),
            Screening.create(
                movie = movie2,
                startTime =
                    ScreeningStartTime(
                        LocalDateTime.of(2026, 4, 10, 11, 30),
                    ),
            ),
            Screening.create(
                movie = movie2,
                startTime =
                    ScreeningStartTime(
                        LocalDateTime.of(2026, 4, 10, 17, 0),
                    ),
            ),
            Screening.create(
                movie = movie3,
                startTime =
                    ScreeningStartTime(
                        LocalDateTime.of(2026, 4, 10, 13, 0),
                    ),
            ),
            Screening.create(
                movie = movie3,
                startTime =
                    ScreeningStartTime(
                        LocalDateTime.of(2026, 4, 10, 19, 30),
                    ),
            ),
        )
    }
}
