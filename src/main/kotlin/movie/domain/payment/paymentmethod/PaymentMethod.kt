package movie.domain.payment.paymentmethod

import movie.constants.ErrorMessages

abstract class PaymentMethod {
    abstract val rate: Double

    fun calculateDiscount(price: Int): Int = (price * (1 - rate)).toInt()

    companion object {
        fun classifyPaymentMethod(number: Int): PaymentMethod =
            when (number) {
                1 -> CreditCard()
                2 -> Cash()
                else -> throw IllegalArgumentException(ErrorMessages.INVALID_PAYMENT_METHOD.message)
            }
    }
}
