package constants

enum class ErrorMessages(val message: String) {
    SCREENING_DOES_NOT_EXIST("해당 조건의 상영이 없습니다."),
    INVALID_DATE_FORMAT("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD)"),
    SELECTED_RESERVED_SEAT("이미 예약된 좌석은 다시 선택할 수 없습니다."),
    SELECT_SAME_SEAT("동일한 좌석을 선택할 수 없습니다."),
    INCORRECT_SEAT_NUMBER("올바른 좌석 번호를 입력해주세요."),
    OVERLAP_MOVIE_TIME("선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."),
    INCORRECT_SCREENING_NUMBER("올바른 상영 번호를 선택해 주세요."),
    INVALID_USING_POINT("올바른 포인트 사용액수가 아닙니다."),
    NOT_ENOUGH_POINT("보유 포인트가 부족합니다."),
    SHOULD_POINT_OVER_ZERO("포인트는 0원 이상이어야 합니다."),
    INVALID_PAYMENT_METHOD("올바른 결제 수단을 선택해 주세요."),
    INVALID_ROW("유효하지 않은 행 인덱스입니다."),
    INVALID_COL("유효하지 않은 열 인덱스입니다."),
    INVALID_SEAT("유효하지 않은 좌석 위치입니다."),
    NOT_EXIST_SEAT("존재하지 않는 좌석입니다: "),
    PAY_FAIL("결제에 실패했습니다."),

}
