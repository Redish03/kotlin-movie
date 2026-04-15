package domain.reservation

import constants.ErrorMessages
import domain.screening.Screening

class Cart(
    private val reservedScreen: List<ReservedScreen> = emptyList(),
) {
    val items: List<ReservedScreen> = reservedScreen.toList()

    fun checkScreeningOverlap(target: Screening) {
        items.forEach {
            require(!it.screen.overlaps(target)) {
                ErrorMessages.OVERLAP_MOVIE_TIME.message
            }
        }
    }

    fun add(screening: ReservedScreen): Cart {
        checkScreeningOverlap(screening.screen)
        return Cart(reservedScreen + screening)
    }
}
