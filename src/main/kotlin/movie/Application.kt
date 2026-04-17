package movie

import movie.controller.ScreeningMockData
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.sql.Statement
import java.sql.Timestamp
import java.util.concurrent.atomic.AtomicLong

@SpringBootApplication
open class Application {
    @org.springframework.context.annotation.Bean
    open fun dataSource(): javax.sql.DataSource {
        val dataSource =
            object : javax.sql.DataSource {
                override fun getConnection(): java.sql.Connection = movie.database.DatabaseConnection.getConnection()

                override fun getConnection(
                    username: String?,
                    password: String?,
                ): java.sql.Connection = getConnection()

                override fun getLogWriter(): java.io.PrintWriter? = null

                override fun setLogWriter(out: java.io.PrintWriter?) {}

                override fun setLoginTimeout(seconds: Int) {}

                override fun getLoginTimeout(): Int = 0

                override fun <T : Any?> unwrap(iface: Class<T>?): T = throw java.lang.UnsupportedOperationException()

                override fun isWrapperFor(iface: Class<*>?): Boolean = false

                override fun getParentLogger(): java.util.logging.Logger = throw java.sql.SQLFeatureNotSupportedException()
            }

        // 데이터베이스 동기화 로직 (기존 데이터 유지 + 신규 Mock 데이터 추가)
        dataSource.connection.use { conn ->
            movie.database.DatabaseConnection.initSchema(conn)

            val mockScreenings = ScreeningMockData.screenings()
            val mockMovies = mockScreenings.map { it.movie }.distinctBy { it.title.value }

            val movieMap = mutableMapOf<String, Long>()

            // 영화 정보 동기화
            val findMovieStmt = conn.prepareStatement("SELECT id FROM movie WHERE title = ?")
            val insertMovieStmt =
                conn.prepareStatement(
                    "INSERT INTO movie (title, running_time) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS,
                )

            mockMovies.forEach { movie ->
                findMovieStmt.setString(1, movie.title.value)
                val rs = findMovieStmt.executeQuery()
                if (rs.next()) {
                    movieMap[movie.title.value] = rs.getLong(1)
                } else {
                    insertMovieStmt.setString(1, movie.title.value)
                    insertMovieStmt.setInt(2, movie.runningTime.value)
                    insertMovieStmt.executeUpdate()
                    val keys = insertMovieStmt.generatedKeys
                    if (keys.next()) {
                        movieMap[movie.title.value] = keys.getLong(1)
                    }
                }
            }

            // 상영 정보 동기화
            val findScreeningStmt =
                conn.prepareStatement("SELECT id FROM screening WHERE movie_id = ? AND start_time = ?")
            val insertScreeningStmt =
                conn.prepareStatement("INSERT INTO screening (movie_id, start_time) VALUES (?, ?)")

            mockScreenings.forEach { screening ->
                val movieId = movieMap[screening.movie.title.value]
                if (movieId != null) {
                    val startTime = Timestamp.valueOf(screening.startTime.value)
                    findScreeningStmt.setLong(1, movieId)
                    findScreeningStmt.setTimestamp(2, startTime)
                    val rs = findScreeningStmt.executeQuery()

                    if (!rs.next()) {
                        // DB에 없는 상영 시간인 경우에만 추가
                        insertScreeningStmt.setLong(1, movieId)
                        insertScreeningStmt.setTimestamp(2, startTime)
                        insertScreeningStmt.executeUpdate()
                    }
                }
            }
        }

        return dataSource
    }
}

data class Greeting(
    val id: Long,
    val content: String,
)

@RestController
class GreetingController {
    private val counter = AtomicLong()

    @GetMapping("/greeting")
    fun greeting(
        @RequestParam name: String = "World",
    ) = Greeting(counter.incrementAndGet(), name)
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
