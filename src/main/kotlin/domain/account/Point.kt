package domain.account

import constants.ErrorMessages

class Point(
    val amount: Int,
) {
    init {
        require(amount >= 0) { ErrorMessages.SHOULD_POINT_OVER_ZERO.message }
    }

    fun usePoint(usedAmount: Int): Point = Point(amount - usedAmount)
}
