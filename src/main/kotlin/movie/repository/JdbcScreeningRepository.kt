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
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate
import javax.sql.DataSource

@Repository
class JdbcScreeningRepository(
    private val dataSource: DataSource,
) : ScreeningRepository {
    override fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate,
    ): List<Screening> {
        val sql =
            """
            SELECT s.id AS screening_id, s.start_time, m.id AS movie_id, m.title, m.running_time
            FROM screening s
            JOIN movie m ON s.movie_id = m.id
            WHERE m.title = ? AND CAST(s.start_time AS DATE) = ?
            ORDER BY s.start_time
            """.trimIndent()

        val screenings = mutableListOf<Screening>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, title)
                pstmt.setDate(2, java.sql.Date.valueOf(date))
                pstmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        screenings.add(mapToScreening(rs))
                    }
                }
            }
        }

        require(screenings.isNotEmpty()) { ErrorMessages.SCREENING_DOES_NOT_EXIST.message }
        return screenings
    }

    override fun findSelectedScreening(
        selectedNumber: Int,
        availableScreenings: List<Screening>,
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

        val newSeats =
            updatedScreening.reservedSeats.filter { newSeat ->
                existingSeats.none { it.seatNumber == newSeat.seatNumber }
            }

        if (newSeats.isNotEmpty()) {
            val sql = "INSERT INTO reservation (screening_id, seat_row, seat_column, seat_grade) VALUES (?, ?, ?, ?)"

            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    for (seat in newSeats) {
                        pstmt.setLong(1, screeningId)
                        pstmt.setString(2, seat.row.value)
                        pstmt.setInt(3, seat.column.value)
                        pstmt.setString(4, seat.grade.name)
                        pstmt.addBatch()
                    }
                    pstmt.executeBatch()
                }
            }
        }
    }

    override fun findAll(): List<Screening> {
        val sql =
            """
            SELECT s.id AS screening_id, s.start_time, m.id AS movie_id, m.title, m.running_time
            FROM screening s
            JOIN movie m ON s.movie_id = m.id
            ORDER BY s.start_time
            """.trimIndent()

        val screenings = mutableListOf<Screening>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        screenings.add(mapToScreening(rs))
                    }
                }
            }
        }
        return screenings
    }

    override fun findById(id: Long): Screening? {
        val sql =
            """
            SELECT s.id AS screening_id, s.start_time, m.id AS movie_id, m.title, m.running_time
            FROM screening s
            JOIN movie m ON s.movie_id = m.id
            WHERE s.id = ?
            """.trimIndent()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setLong(1, id)
                pstmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return mapToScreening(rs)
                    }
                }
            }
        }
        return null
    }

    private fun findReservedSeats(screeningId: Long): List<Seat> {
        val sql = "SELECT seat_row, seat_column, seat_grade FROM reservation WHERE screening_id = ?"
        val seats = mutableListOf<Seat>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setLong(1, screeningId)
                pstmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        seats.add(
                            Seat(
                                SeatRow(rs.getString("seat_row")),
                                SeatColumn(rs.getInt("seat_column")),
                                SeatGrade.valueOf(rs.getString("seat_grade")),
                            ),
                        )
                    }
                }
            }
        }
        return seats
    }

    private fun mapToScreening(rs: ResultSet): Screening {
        val screeningId = rs.getLong("screening_id")
        val movie =
            Movie(
                id = rs.getLong("movie_id"),
                title = MovieTitle(rs.getString("title")),
                runningTime = RunningTime(rs.getInt("running_time")),
            )
        return Screening.create(
            id = screeningId,
            movie = movie,
            startTime = ScreeningStartTime(rs.getTimestamp("start_time").toLocalDateTime()),
            reservedSeats = findReservedSeats(screeningId),
        )
    }
}
