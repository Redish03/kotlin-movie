package movie.repository

import movie.domain.screening.Screening
import java.time.LocalDate

interface ScreeningRepository {
    fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate,
    ): List<Screening>

    fun findSelectedScreening(
        selectedNumber: Int,
        availableScreenings: List<Screening>,
    ): Screening

    fun updateScreening(updatedScreening: Screening)

    fun findAll(): List<Screening>

    fun findById(id: Long): Screening?
}
