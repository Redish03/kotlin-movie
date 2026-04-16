package repository

import domain.screening.Screening
import java.time.LocalDate

interface ScreeningRepository {
    fun findByMovieTitleAndDate(title: String, date: LocalDate): List<Screening>

    fun findSelectedScreening(selectedNumber: Int, availableScreenings: List<Screening>) : Screening

    fun updateScreening(updatedScreening: Screening)
}
