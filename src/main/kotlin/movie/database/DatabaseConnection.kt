package movie.database

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConnection {
    // 로컬 실행 URL
    private const val LOCAL_URL = "jdbc:h2:~/kotlin-movie;AUTO_SERVER=TRUE"
    private const val TEST_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"

    fun getConnection(isTest: Boolean = false): Connection {
        val url = if (isTest) TEST_URL else LOCAL_URL
        return DriverManager.getConnection(url, "sa", "")
    }

    fun initSchema(connection: Connection) {
        val statement = connection.createStatement()
        statement.execute(
            """
            CREATE TABLE IF NOT EXISTS movie (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                running_time INT NOT NULL
            );
            
            CREATE TABLE IF NOT EXISTS screening (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                movie_id BIGINT NOT NULL,
                start_time TIMESTAMP NOT NULL,
                FOREIGN KEY (movie_id) REFERENCES movie(id)
            );
            
            CREATE TABLE IF NOT EXISTS reservation (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                screening_id BIGINT NOT NULL,
                seat_row VARCHAR(10) NOT NULL,
                seat_column INT NOT NULL,
                seat_grade VARCHAR(10) NOT NULL,
                FOREIGN KEY (screening_id) REFERENCES screening(id)
            );
            """.trimIndent(),
        )
    }
}
