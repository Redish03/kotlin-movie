package movie.domain.repository

import movie.database.DatabaseConnection
import movie.repository.JdbcScreeningRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.PrintWriter
import java.sql.Connection
import java.sql.SQLFeatureNotSupportedException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.logging.Logger
import javax.sql.DataSource

class JdbcScreeningRepositoryTest {
    private lateinit var connection: Connection
    private lateinit var dataSource: DataSource
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

        // Simple DataSource wrapper for testing
        dataSource =
            object : DataSource {
                override fun getConnection(): Connection = DatabaseConnection.getConnection(isTest = true)

                override fun getConnection(
                    username: String?,
                    password: String?,
                ): Connection = getConnection()

                override fun getLogWriter(): PrintWriter? = null

                override fun setLogWriter(out: PrintWriter?) {}

                override fun setLoginTimeout(seconds: Int) {}

                override fun getLoginTimeout(): Int = 0

                override fun <T : Any?> unwrap(iface: Class<T>?): T = throw UnsupportedOperationException()

                override fun isWrapperFor(iface: Class<*>?): Boolean = false

                override fun getParentLogger(): Logger = throw SQLFeatureNotSupportedException()
            }

        repository = JdbcScreeningRepository(dataSource)
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
    fun `DB에 저장된 모든 상영 목록을 반환한다`() {
        // given
        val statement = connection.createStatement()
        statement.execute("INSERT INTO movie (id, title, running_time) VALUES (2, '인셉션', 148)")
        statement.execute("INSERT INTO screening (id, movie_id, start_time) VALUES (2, 2, '2026-04-11 15:00:00')")

        // when
        val screenings = repository.findAll()

        // then
        assertThat(screenings).hasSize(2)
        assertThat(screenings.map { it.movie.title.value }).containsExactlyInAnyOrder("어벤져스", "인셉션")
    }
}
