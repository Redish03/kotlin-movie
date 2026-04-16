package domain.repository

import database.DatabaseConnection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import repository.JdbcScreeningRepository
import java.sql.Connection
import java.time.LocalDate
import java.time.LocalDateTime

class JdbcScreeningRepositoryTest {

    private lateinit var connection: Connection
    private lateinit var repository: JdbcScreeningRepository

    @BeforeEach
    fun setUp() {
        connection = DatabaseConnection.getConnection(isTest = true)

        DatabaseConnection.initSchema(connection)

        val statement = connection.createStatement()
        statement.execute("DELETE FROM reservation")
        statement.execute("DELETE FROM screening")
        statement.execute("DELETE FROM movie")

        statement.execute("INSERT INTO movie (id, title, running_time) VALUES (1, '어벤져스', 120)")
        statement.execute("INSERT INTO screening (id, movie_id, start_time) VALUES (1, 1, '2026-04-10 10:00:00')")

        repository = JdbcScreeningRepository(connection)
    }

    @AfterEach
    fun tearDown() {
        connection.close()
    }

    @Test
    fun `영화 제목과 날짜가 주어지면 DB에서 해당하는 상영 목록을 찾아 도메인 객체로 반환한다`() {
        // given
        val title = "어벤져스"
        val date = LocalDate.of(2026, 4, 10)

        // when
        val screenings = repository.findByMovieTitleAndDate(title, date)

        // then
        assertThat(screenings).hasSize(1)
        assertThat(screenings[0].movie.title.value).isEqualTo("어벤져스")
        assertThat(screenings[0].startTime.value).isEqualTo(LocalDateTime.of(2026, 4, 10, 10, 0))
    }

    @Test
    fun `예약 정보를 DB에 정상적으로 저장한다`() {
        // given
        val screeningId = 1L
        val seatRow = "A"
        val seatColumn = 1
        val seatGrade = "S"

        val insertQuery = "INSERT INTO reservation (screening_id, seat_row, seat_column, seat_grade) VALUES (?, ?, ?, ?)"
        val pstmt = connection.prepareStatement(insertQuery)
        pstmt.setLong(1, screeningId)
        pstmt.setString(2, seatRow)
        pstmt.setInt(3, seatColumn)
        pstmt.setString(4, seatGrade)
        pstmt.executeUpdate()

        val statement = connection.createStatement()
        val rs = statement.executeQuery("SELECT * FROM reservation WHERE screening_id = 1")

        assertThat(rs.next()).isTrue() // 데이터가 존재해야 함
        assertThat(rs.getString("seat_row")).isEqualTo("A")
        assertThat(rs.getInt("seat_column")).isEqualTo(1)
        assertThat(rs.getString("seat_grade")).isEqualTo("S")
    }
}
