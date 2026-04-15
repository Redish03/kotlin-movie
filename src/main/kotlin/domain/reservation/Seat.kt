package domain.reservation

import constants.ErrorMessages

data class Seat(
    val row: SeatRow,
    val column: SeatColumn,
    val grade: SeatGrade,
) {
    val seatNumber: String by lazy { "${row.value}${column.value}" }

    companion object {
        fun create(
            rowIndex: Int,
            columnIndex: Int,
        ): Seat {
            val row = SeatRow.setRow(rowIndex)
            val column = SeatColumn.setCol(columnIndex)
            val grade = SeatGrade.grantGrade(rowIndex, columnIndex)

            return Seat(
                row = row,
                column = column,
                grade = grade,
            )
        }
    }
}

@JvmInline
value class SeatRow(
    val value: String,
) {
    companion object {
        fun setRow(rowIndex: Int): SeatRow {
            require(rowIndex in 0..4) { ErrorMessages.INVALID_ROW.message }
            return SeatRow(('A' + rowIndex).toString())
        }
    }
}

@JvmInline
value class SeatColumn(
    val value: Int,
) {
    companion object {
        fun setCol(columnIndex: Int): SeatColumn {
            require(columnIndex in 0..3) { ErrorMessages.INVALID_COL.message }
            return SeatColumn(columnIndex + 1)
        }
    }
}
