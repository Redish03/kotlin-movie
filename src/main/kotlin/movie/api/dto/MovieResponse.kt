package movie.api.dto

data class MovieResponse(
    val id: Long,
    val title: String,
    val runningTimeMinutes: Int,
    val screenings: List<ScreeningResponse>,
)
