package movie.domain.payment

import movie.constants.ErrorMessages
import movie.domain.account.Account
import movie.domain.payment.discount.DiscountPolicy
import movie.domain.payment.discount.MovieDayDiscountPolicy
import movie.domain.payment.discount.TimeDiscountPolicy
import movie.domain.payment.paymentmethod.PaymentMethod
import movie.domain.reservation.Cart
import movie.domain.reservation.ReservedScreen

class Payment(
    val cart: Cart,
) {
    fun pay(
        pointAmount: Int = 0,
        account: Account,
        selectedPaymentMethod: PaymentMethod,
    ): PayResult =
        runCatching {
            val discountPolicies = listOf(MovieDayDiscountPolicy(), TimeDiscountPolicy())
            val discountedByDateAmount = discountedTotalAmount(discountPolicies)
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

    fun discountedTotalAmount(applyingPolicies: List<DiscountPolicy>): Int {
        var totalAmount = 0
        cart.items.forEach { reservedScreen ->
            val discountAmount = applyDiscounts(applyingPolicies, reservedScreen, reservedScreen.price())
            totalAmount += discountAmount
        }
        return totalAmount
    }

    private fun applyDiscounts(
        applyingPolicies: List<DiscountPolicy>,
        reservedScreen: ReservedScreen,
        money: Int,
    ): Int {
        var result = money
        applyingPolicies.forEach { result = it.discount(reservedScreen, result) }
        return result
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
