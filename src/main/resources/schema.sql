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
