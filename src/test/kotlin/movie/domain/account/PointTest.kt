package movie.domain.account

import movie.domain.account.Point
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PointTest {
    @Test
    fun `포인트가 사용한 만큼 차감된다`() {
        val useAmount: Int = 1000
        val point = Point(2000)
        val usedPoint = point.usePoint(useAmount)
        assertEquals(useAmount, usedPoint.amount)
    }

    @Test
    fun `보유 포인트보다 사용 포인트 액수가 많을 시 예외가 발생한다`() {
        val useAmount: Int = 3000
        val point = Point(2000)

        assertThrows<IllegalArgumentException> { point.usePoint(useAmount) }
    }
}
