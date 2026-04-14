package domain.payment

import constants.ErrorMessages
import domain.account.Account
import domain.payment.discount.DiscountPolicy
import domain.payment.discount.MovieDayDiscountPolicy
import domain.payment.discount.TimeDiscountPolicy
import domain.payment.paymentmethod.PaymentMethod
import domain.reservation.Cart
import domain.reservation.ReservedScreen

class Payment(
    val cart: Cart,
) {
    fun pay(
        pointAmount: Int = 0,
        account: Account,
        selectedPaymentMethod: PaymentMethod,
    ): PayResult =
        runCatching {
            val discountedByDateAmount = discountedTotalAmount()
            val amountAfterPoint = applyPoint(discountedByDateAmount, account, pointAmount)
            val paidAmount = selectPaymentMethod(amountAfterPoint, selectedPaymentMethod)

            PayResult.Success(
                cart = cart,
                paidAmount = paidAmount,
                usedPoint = pointAmount,
                paymentMethod = selectedPaymentMethod,
            )
        }.getOrElse { exception ->
            PayResult.Failure(
                message = exception.message ?: ErrorMessages.PAY_FAIL.message,
            )
        }

    fun discountedTotalAmount(): Int {
        var totalAmount = 0
        cart.items.forEach { reservedScreen ->
            val discountedByDateAmount =
                applyDiscounts(MovieDayDiscountPolicy(), reservedScreen, reservedScreen.price())
            val discountedByTimeAmount =
                applyDiscounts(TimeDiscountPolicy(), reservedScreen, discountedByDateAmount)
            totalAmount += discountedByTimeAmount
        }
        return totalAmount
    }

    private fun applyDiscounts(
        discountPolicy: DiscountPolicy,
        reservedScreen: ReservedScreen,
        money: Int,
    ): Int {
        return discountPolicy.discount(reservedScreen, money)
    }

    private fun applyPoint(
        amount: Int,
        account: Account,
        point: Int,
    ): Int {
        account.useMyPoint(point)
        return amount - point
    }

    private fun selectPaymentMethod(
        amount: Int,
        paymentMethod: PaymentMethod,
    ): Int = paymentMethod.calculateDiscount(amount)
}

sealed interface PayResult {
    data class Success(
        val cart: Cart,
        val paidAmount: Int,
        val usedPoint: Int,
        val paymentMethod: PaymentMethod,
    ) : PayResult

    data class Failure(
        val message: String,
    ) : PayResult
}
