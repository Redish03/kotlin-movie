package movie.domain.payment.discount

import movie.domain.reservation.ReservedScreen

interface DiscountPolicy {
    fun discount(
        reservedScreen: ReservedScreen,
        money: Int,
    ): Int

    fun isDiscountable(reservedScreen: ReservedScreen): Boolean
}
