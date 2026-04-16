package repository

import constants.ErrorMessages
import domain.screening.Screening
import java.sql.Connection
import java.time.LocalDate

class JdbcScreeningRepository(
    private val connection: Connection
) : ScreeningRepository {
    override fun findByMovieTitleAndDate(
        title: String,
        date: LocalDate
    ): List<Screening> {
        // Todo: JDBC활용 select 쿼리를 가져와 screening 도메인 객체로 변환해 반환
        return emptyList()
    }

    override fun findSelectedScreening(
        selectedNumber: Int,
        availableScreenings: List<Screening>
    ): Screening {
//        todo: 메모리에 로드된 리스트에서 선택하는 로직, screenings랑 동일하게 유지
        require(selectedNumber in 1..availableScreenings.size) {
            ErrorMessages.INCORRECT_SCREENING_NUMBER.message
        }

        return availableScreenings[selectedNumber - 1]
    }

    override fun updateScreening(updatedScreening: Screening) {
//        todo: jdbc 써서 insert, update 로 예약된 좌석 정보를 db에 저장
    }
}
