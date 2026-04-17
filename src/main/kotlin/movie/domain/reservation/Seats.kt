package movie.domain.reservation

import movie.constants.ErrorMessages

class Seats private constructor(
    private val values: List<Seat>,
) {
    fun allSeats(): List<Seat> = values.toList()

    private fun findBySeatNumber(seatNumber: String): Seat =
        values.firstOrNull { it.seatNumber == seatNumber.trim().uppercase() }
            ?: throw IllegalArgumentException(ErrorMessages.NOT_EXIST_SEAT.message + seatNumber)

    fun findAllBySeatNumbers(seatNumbers: List<String>): List<Seat> = seatNumbers.map { findBySeatNumber(it) }

    companion object {
        private const val ROW_SIZE = 5
        private const val COL_SIZE = 4
        private const val TOTAL_SEAT_COUNT = ROW_SIZE * COL_SIZE

        fun create(): Seats {
            val seats = mutableListOf<Seat>()

            for (rowIndex in 0 until ROW_SIZE) {
                for (columnIndex in 0 until COL_SIZE) {
                    seats.add(Seat.create(rowIndex, columnIndex))
                }
            }

            return Seats(seats)
        }
    }
}
