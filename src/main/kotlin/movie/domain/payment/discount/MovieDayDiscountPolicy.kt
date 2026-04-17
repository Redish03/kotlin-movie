package movie.domain.payment.discount

import movie.domain.reservation.ReservedScreen

class MovieDayDiscountPolicy : DiscountPolicy {
    override fun discount(
        reservedScreen: ReservedScreen,
        money: Int,
    ): Int {
        if (isDiscountable(reservedScreen)) return (money * DISCOUNT_PERCENT).toInt()
        return money
    }

    override fun isDiscountable(reservedScreen: ReservedScreen): Boolean =
        MOVIE_DAYS.contains(reservedScreen.screen.startTime.value.dayOfMonth)

    companion object {
        const val DISCOUNT_PERCENT = 0.9
        val MOVIE_DAYS = listOf(10, 20, 30)
    }
}
