package movie.api.dto

import java.time.LocalDateTime

data class ScreeningResponse(
    val id: Long,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
)
