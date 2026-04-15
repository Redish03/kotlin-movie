package repository

import constants.ErrorMessages
import domain.screening.Screening
import java.time.LocalDate

class Screenings(
    val screenings: List<Screening>,
) {
    fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate,
    ): List<Screening> {
        val foundedScreenings = screenings
            .filter {
                it.movie.title.value == title && it.startTime.value.toLocalDate() == date
            }.sortedBy { it.startTime.value }
        require(foundedScreenings.isNotEmpty()) { ErrorMessages.SCREENING_DOES_NOT_EXIST.message }
        return foundedScreenings
    }

    fun findSelectedScreening(
        selectedNumber: Int,
        availableScreenings: List<Screening>,
    ): Screening {
        require(selectedNumber in 1..availableScreenings.size) {
            ErrorMessages.INCORRECT_SCREENING_NUMBER.message
        }

        return availableScreenings[selectedNumber - 1]
    }

    fun updateScreening(updatedScreening: List<Screening>): Screenings =
        Screenings(updatedScreening)
}
