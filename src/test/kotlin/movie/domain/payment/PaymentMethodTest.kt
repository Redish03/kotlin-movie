package movie.domain.payment

import movie.domain.payment.paymentmethod.Cash
import movie.domain.payment.paymentmethod.CreditCard
import movie.domain.payment.paymentmethod.PaymentMethod
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PaymentMethodTest {
    @Test
    fun `신용 카드 결제 선택 시 총액 기준 5% 할인된다`() {
        val totalMoney = 10000
        val creditCardDiscount = CreditCard().calculateDiscount(totalMoney)
        assertTrue(creditCardDiscount == 9500)
    }

    @Test
    fun `현금 결제 선택 시 총액 기준 2% 할인된다`() {
        val totalMoney = 10000
        val creditCardDiscount = Cash().calculateDiscount(totalMoney)
        assertTrue(creditCardDiscount == 9800)
    }

    @Test
    fun `결제 수단을 숫자로 선택한다`() {
        val paymentMethod1 = PaymentMethod.classifyPaymentMethod(1)
        val paymentMethod2 = PaymentMethod.classifyPaymentMethod(2)

        assertTrue(paymentMethod1 is CreditCard)
        assertTrue(paymentMethod2 is Cash)
    }

    @Test
    fun `결제 수단 외 글자가 들어올 시 IllegalArgumentException을 반환한다`() {
        assertThrows<IllegalArgumentException> { PaymentMethod.classifyPaymentMethod(3) }
        assertThrows<IllegalArgumentException> { PaymentMethod.classifyPaymentMethod(123) }
    }
}
