package domain.screening

class Movie(
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
