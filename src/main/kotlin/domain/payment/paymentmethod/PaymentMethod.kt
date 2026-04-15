package domain.payment.paymentmethod

import constants.ErrorMessages

abstract class PaymentMethod {
    abstract val rate: Double
    fun calculateDiscount(price: Int): Int {
        return (price * (1 - rate)).toInt()
    }

    companion object {
        fun classifyPaymentMethod(number: Int): PaymentMethod = when(number) {
            1 -> CreditCard()
            2 -> Cash()
            else -> throw IllegalArgumentException(ErrorMessages.INVALID_PAYMENT_METHOD.message)
        }
    }
}