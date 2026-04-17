package movie.domain.payment

import movie.controller.ScreeningMockData
import movie.domain.account.Account
import movie.domain.account.Point
import movie.domain.payment.discount.MovieDayDiscountPolicy
import movie.domain.payment.discount.TimeDiscountPolicy
import movie.domain.payment.paymentmethod.CreditCard
import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen
import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatGrade
import movie.domain.reservation.SeatRow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PaymentTest {
    private val emptyCart = Cart(emptyList())
    private val containedCart =
        emptyCart.add(
            ReservedScreen(
                screen = ScreeningMockData.screenings().first(),
                seats =
                    listOf(
                        Seat(SeatRow("A"), SeatColumn(2), SeatGrade.S),
                        Seat(SeatRow("B"), SeatColumn(2), SeatGrade.B),
                    ),
            ),
        )
    private val payment =
        Payment(
            cart = emptyCart,
        )

    private val discountPolicies = listOf(MovieDayDiscountPolicy(), TimeDiscountPolicy())

    @Test
    fun `카트에 담긴 좌석들의 날짜 및 시간 할인 적용 금액을 계산한다`() {
        val newPayment =
            Payment(
                cart = containedCart,
            )

        assertEquals(25000, newPayment.discountedTotalAmount(discountPolicies))
    }

    @Test
    fun `여러 할인 혜택과 포인트를 적용한 후 최종 금액을 계산한다`() {
        val newPayment =
            Payment(
                cart = containedCart,
            )

        val result =
            newPayment.pay(
                pointAmount = 0,
                account = Account(),
                selectedPaymentMethod = CreditCard(),
            )

        assertEquals(23_750, (result as PayResult.Success).paidAmount)
    }

    @Test
    fun `결제에 실패하면 IllegalArgumentException을 발생시킨다`() {
        val result1 =
            payment.pay(
                pointAmount = 3000,
                account = Account(),
                selectedPaymentMethod = CreditCard(),
            )
        val result2 =
            payment.pay(
                pointAmount = 2000,
                account = Account(Point(0)),
                selectedPaymentMethod = CreditCard(),
            )

        assertTrue(result1 is PayResult.Failure)
        assertTrue(result2 is PayResult.Failure)
    }
}
