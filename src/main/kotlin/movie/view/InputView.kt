package movie.view

import movie.constants.ErrorMessages
import java.time.LocalDate

class InputView {
    fun isReservationStarted(): Boolean {
        println("영화 예매를 시작합니다. 새 예매를 생성하시겠습니까? (Y/N)")
        return readln().trim().uppercase() == CONFIRM_INPUT
    }

    fun isAddMoreMovie(): Boolean {
        println("다른 영화를 추가하시겠습니까? (Y/N)")
        return readln().trim().uppercase() == CONFIRM_INPUT
    }

    fun isConfirmPay(): Boolean {
        println("위 금액으로 결제하시겠습니까? (Y/N)")
        return readln().trim().uppercase() == CONFIRM_INPUT
    }

    fun readMovieTitle(): String {
        println("예매할 영화 제목을 입력하세요:")
        return readln().trim()
    }

    fun readDate(): String {
        println("날짜를 입력하세요 (YYYY-MM-DD):")
        val date = readln().trim()
        try {
            LocalDate.parse(date)
        } catch (e: Exception) {
            throw IllegalArgumentException(ErrorMessages.INVALID_DATE_FORMAT.message)
        }
        return date
    }

    fun readScreeningNumber(): Int {
        println("상영 번호를 선택하세요:")
        return readln().trim().toInt()
    }

    fun readSeatNumbers(): List<String> {
        println("예약할 좌석을 입력하세요 (A1, B2):")
        val input = readln().trim()
        require(input.isNotBlank()) { ErrorMessages.INCORRECT_SEAT_NUMBER.message }

        val convertedInput =
            input
                .split(SEAT_NUMBER_PARSER)
                .map { it.trim().uppercase() }
                .filter { it.isNotBlank() }

        require(convertedInput.toSet().size == convertedInput.size) {
            ErrorMessages.SELECT_SAME_SEAT.message
        }

        return convertedInput
    }

    fun readPointAmount(): Int {
        println("사용할 포인트를 입력하세요 (없으면 0):")
        val point = readln().trim().toIntOrNull()
        require(point != null && point >= 0) { "올바른 포인트 액수를 입력해주세요." }
        return point
    }

    fun readPaymentMethod(): Int {
        println("결제 수단을 선택하세요:")
        println("1) 신용카드(5% 할인)")
        println("2) 현금(2% 할인)")
        return readln().trim().toInt()
    }

    companion object {
        private const val CONFIRM_INPUT = "Y"
        const val SEAT_NUMBER_PARSER = ","
    }
}
