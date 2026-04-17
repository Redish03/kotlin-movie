package movie.api.dto

data class ReservationItemRequest(
    val screeningId: Long,
    val seats: List<String>,
)
