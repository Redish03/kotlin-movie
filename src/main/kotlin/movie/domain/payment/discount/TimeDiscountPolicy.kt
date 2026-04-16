package movie.domain.payment.discount

import movie.domain.reservation.ReservedScreen

class TimeDiscountPolicy : DiscountPolicy {
    override fun discount(
        reservedScreen: ReservedScreen,
        money: Int,
    ): Int {
        if (isDiscountable(reservedScreen)) return money - SALE_AMOUNT
        return money
    }

    override fun isDiscountable(reservedScreen: ReservedScreen): Boolean =
        reservedScreen.screen.startTime.value.hour !in GENERAL_SALE_START_TIME..GENERAL_SALE_END_TIME

    companion object {
        const val GENERAL_SALE_END_TIME = 19
        const val GENERAL_SALE_START_TIME = 11
        const val SALE_AMOUNT = 2000
    }
}
