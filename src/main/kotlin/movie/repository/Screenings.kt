package movie.repository

import movie.constants.ErrorMessages
import movie.domain.screening.Screening
import java.time.LocalDate

class Screenings(
    var screenings: List<Screening>,
) : ScreeningRepository {
    override fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate,
    ): List<Screening> {
        val foundedScreenings =
            screenings
                .filter {
                    it.movie.title.value == title && it.startTime.value.toLocalDate() == date
                }.sortedBy { it.startTime.value }
        require(foundedScreenings.isNotEmpty()) { ErrorMessages.SCREENING_DOES_NOT_EXIST.message }
        return foundedScreenings
    }

    override fun findSelectedScreening(
        selectedNumber: Int,
        availableScreenings: List<Screening>,
    ): Screening {
        require(selectedNumber in 1..availableScreenings.size) {
            ErrorMessages.INCORRECT_SCREENING_NUMBER.message
        }

        return availableScreenings[selectedNumber - 1]
    }

    override fun updateScreening(updatedScreening: Screening) {
        screenings =
            screenings.map {
                if (it.movie == updatedScreening.movie && it.startTime == updatedScreening.startTime) updatedScreening else it
            }
    }

    override fun findAll(): List<Screening> = screenings.toList()

    override fun findById(id: Long): Screening? = screenings.find { it.id == id }
}
