package movie.domain.screening

class Movie(
    val id: Long = 0L,
    val title: MovieTitle,
    val runningTime: RunningTime,
)

@JvmInline
value class MovieTitle(
    val value: String,
)

@JvmInline
value class RunningTime(
    val value: Int,
)
