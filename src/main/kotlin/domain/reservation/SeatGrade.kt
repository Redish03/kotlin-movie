package domain.reservation

import constants.ErrorMessages

enum class SeatGrade(
    val money: Int,
) {
    S(money = 18_000),
    A(money = 15_000),
    B(money = 12_000),
    ;

    companion object {
        private const val ROW_SIZE = 5
        private const val COLUMN_SIZE = 4

        fun grantGrade(
            rowIndex: Int,
            columnIndex: Int,
        ): SeatGrade {
            require(rowIndex in 0 until ROW_SIZE) { ErrorMessages.INVALID_ROW.message }
            require(columnIndex in 0 until COLUMN_SIZE) { ErrorMessages.INVALID_COL.message }

            return when (rowIndex) {
                0, 1 -> B
                2, 3 -> S
                4 -> A
                else -> throw IllegalArgumentException(ErrorMessages.INVALID_SEAT.message)
            }
        }
    }
}
