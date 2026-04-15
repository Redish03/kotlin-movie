package domain.account

import constants.ErrorMessages

class Account(
    var point: Point = Point(0),
) {
    fun useMyPoint(usingPoints: Int) {
        require(usingPoints >= 0) { ErrorMessages.INVALID_USING_POINT.message }
        require(point.amount >= usingPoints) { ErrorMessages.NOT_ENOUGH_POINT.message }
        point = point.usePoint(usingPoints)
    }
}
