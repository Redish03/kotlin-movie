package movie.view

import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seat
import movie.domain.reservation.Seats
import movie.domain.screening.Screening
import java.time.format.DateTimeFormatter

class OutputView {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun printScreenings(screenings: List<Screening>) {
        println("해당 날짜의 상영 목록")
        screenings.forEachIndexed { idx, screening ->
            println("[${idx + 1}] ${screening.startTime.value.toLocalTime()}")
        }
    }

    fun printSeatLayout(
        allSeats: Seats,
        reservedSeats: List<Seat>,
    ) {
        println("좌석 배치도")
        println("    1    2    3    4")

        val grouped = allSeats.allSeats().groupBy { it.row.value }
        grouped.forEach { (row, seats) ->
            val seatText =
                seats.joinToString(" ") { seat ->
                    if (reservedSeats.contains(seat)) {
                        "[ X]"
                    } else {
                        "[ ${seat.grade.name}]"
                    }
                }
            println("$row $seatText")
        }
    }

    fun printCartAdded(item: ReservedScreen) {
        val seats = item.seats.joinToString(", ") { it.seatNumber }
        println("장바구니에 추가됨")
        println(
            "- [${item.screen.movie.title.value}] ${
                item.screen.startTime.value.format(dateTimeFormatter)
            }  좌석: $seats",
        )
    }

    fun printCart(cart: Cart) {
        println("장바구니")
        cart.items.forEach {
            val seats = it.seats.joinToString(", ") { seat -> seat.seatNumber }
            println(
                "- [${it.screen.movie.title.value}] ${
                    it.screen.startTime.value.format(dateTimeFormatter)
                }  좌석: $seats",
            )
        }
    }

    fun printTotalCost(totalCost: Int) {
        println("가격 계산")
        println("최종 결제 금액: ${"%,d".format(totalCost)}원")
    }

    fun printEndTicketing() {
        println("예매를 종료합니다.")
    }

    fun printCancelPay() {
        println("결제가 취소되었습니다.")
    }

    fun printFinishReservationMessage() {
        println("예매 완료")
        println("내역:")
    }

    fun printTicketReservationInformation(
        seatsInfos: List<Seat>,
        screeningInfo: Screening,
    ) {
        println(formatSeatsAndScreeningInfo(seatsInfos, screeningInfo))
    }

    fun printPaymentResult(
        amount: Int,
        usedPoint: Int,
    ) {
        println("결제 금액: ${amount.toDecimalFormat()}원 (포인트 ${usedPoint.toDecimalFormat()}원 사용)")
        println()
        println("감사합니다.")
    }

    fun printErrorMessage(message: String) {
        println(message)
    }

    private fun formatSeatsAndScreeningInfo(
        seats: List<Seat>,
        screeningInfos: Screening,
    ): String {
        val seats = seats.joinToString(", ") { it.seatNumber }
        val screeningInfo =
            "- [${screeningInfos.movie.title.value}]" +
                " ${screeningInfos.startTime.value.toLocalDate()} " +
                "${screeningInfos.startTime.value.toLocalTime()}"

        return screeningInfo + "좌석: " + seats
    }

    fun Int.toDecimalFormat(): String = "%,d".format(this)
}
