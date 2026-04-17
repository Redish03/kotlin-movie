package movie.repository

import movie.constants.ErrorMessages
import movie.domain.reservation.Seat
import movie.domain.reservation.SeatColumn
import movie.domain.reservation.SeatGrade
import movie.domain.reservation.SeatRow
import movie.domain.screening.Movie
import movie.domain.screening.MovieTitle
import movie.domain.screening.RunningTime
import movie.domain.screening.Screening
import movie.domain.screening.ScreeningStartTime
import java.sql.Connection
import java.time.LocalDate

class JdbcScreeningRepository(
    private val connection: Connection
) : ScreeningRepository {
    override fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate
    ): List<Screening> {
        val sql = """
            SELECT s.id AS screening_id, s.start_time, m.id AS movie_id, m.title, m.running_time
            FROM screening s
            JOIN movie m ON s.movie_id = m.id
            WHERE m.title = ? AND CAST(s.start_time AS DATE) = ?
            ORDER BY s.start_time
        """.trimIndent()

        val screenings = mutableListOf<Screening>()

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, title)
            statement.setDate(2, java.sql.Date.valueOf(date))

            statement.executeQuery().use { rs ->
                while (rs.next()) {
                    val screeningId = rs.getLong("screening_id")

                    val movie = Movie(
                        id = rs.getLong("movie_id"),
                        title = MovieTitle(rs.getString("title")),
                        runningTime = RunningTime(rs.getInt("running_time"))
                    )

                    val reservedSeats = findReservedSeats(screeningId)

                    screenings.add(
                        Screening.create(
                            id = screeningId,
                            movie = movie,
                            startTime = ScreeningStartTime(rs.getTimestamp("start_time").toLocalDateTime()),
                            reservedSeats = reservedSeats
                        )
                    )
                }
            }
        }

        require(screenings.isNotEmpty()) { ErrorMessages.SCREENING_DOES_NOT_EXIST.message }
        return screenings
    }

    override fun findSelectedScreening(
        selectedNumber: Int,
        availableScreenings: List<Screening>
    ): Screening {
        require(selectedNumber in 1..availableScreenings.size) {
            ErrorMessages.INCORRECT_SCREENING_NUMBER.message
        }

        return availableScreenings[selectedNumber - 1]
    }

    override fun updateScreening(updatedScreening: Screening) {
        val screeningId = updatedScreening.id
        require(screeningId != 0L) { "데이터베이스 식별자가 없는 상영 정보입니다." }

        val existingSeats = findReservedSeats(screeningId)

        val newSeats = updatedScreening.reservedSeats.filter { newSeat ->
            existingSeats.none { it.seatNumber == newSeat.seatNumber }
        }

        if (newSeats.isNotEmpty()) {
            val sql = "INSERT INTO reservation (screening_id, seat_row, seat_column, seat_grade) VALUES (?, ?, ?, ?)"

            connection.prepareStatement(sql).use { statement ->
                for (seat in newSeats) {
                    statement.setLong(1, screeningId) // 이제 식별자를 바로 쓸 수 있습니다!
                    statement.setString(2, seat.row.value)
                    statement.setInt(3, seat.column.value)
                    statement.setString(4, seat.grade.name)
                    statement.addBatch()
                }
                statement.executeBatch()
            }
        }
    }

    private fun findReservedSeats(screeningId: Long): List<Seat> {
        val sql = "SELECT seat_row, seat_column, seat_grade FROM reservation WHERE screening_id = ?"
        val seats = mutableListOf<Seat>()

        connection.prepareStatement(sql).use { pstmt ->
            pstmt.setLong(1, screeningId)
            pstmt.executeQuery().use { rs ->
                while (rs.next()) {
                    val row = rs.getString("seat_row")
                    val column = rs.getInt("seat_column")
                    val grade = SeatGrade.valueOf(rs.getString("seat_grade"))

                    seats.add(Seat(SeatRow(row), SeatColumn(column), grade))
                }
            }
        }
        return seats
    }

    override fun findAll(): List<Screening> {
        val sql = """
            SELECT s.id AS screening_id, s.start_time, m.id AS movie_id, m.title, m.running_time
            FROM screening s
            JOIN movie m ON s.movie_id = m.id
            ORDER BY s.start_time
        """.trimIndent()

        val screenings = mutableListOf<Screening>()

        connection.prepareStatement(sql).use { statement ->
            statement.executeQuery().use { rs ->
                while (rs.next()) {
                    val screeningId = rs.getLong("screening_id")

                    val movie = Movie(
                        id = rs.getLong("movie_id"),
                        title = MovieTitle(rs.getString("title")),
                        runningTime = RunningTime(rs.getInt("running_time"))
                    )

                    val reservedSeats = findReservedSeats(screeningId)

                    screenings.add(
                        Screening.create(
                            id = screeningId,
                            movie = movie,
                            startTime = ScreeningStartTime(rs.getTimestamp("start_time").toLocalDateTime()),
                            reservedSeats = reservedSeats
                        )
                    )
                }
            }
        }
        return screenings
    }
}
