package domain.payment.discount

import domain.reservation.ReservedScreen

interface DiscountPolicy {
    fun discount(
        reservedScreen: ReservedScreen,
        money: Int,
    ): Int

    fun isDiscountable(reservedScreen: ReservedScreen): Boolean
}
