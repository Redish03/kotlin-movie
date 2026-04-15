package domain.reservation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SeatGradeTest {
    @Test
    fun `C열 좌석에는 S 등급이 부여된다`() {
        val grade: SeatGrade = SeatGrade.grantGrade(2, 1)
        assertEquals(SeatGrade.S, grade)
    }

    @Test
    fun `B열 좌석에는 B 등급이 부여된다`() {
        val grade: SeatGrade = SeatGrade.grantGrade(0, 1)
        assertEquals(SeatGrade.B, grade)
    }

    @Test
    fun `E열 좌석에는 A 등급이 부여된다`() {
        val grade: SeatGrade = SeatGrade.grantGrade(4, 1)
        assertEquals(SeatGrade.A, grade)
    }
}
