package movie.api.dto

data class ReservationResponse(
    val reservationId: Long,
    val reservations: List<ReservationItemRequest>,
    val usedPoints: Int,
    val paymentMethod: String,
    val totalPrice: Int,
)
