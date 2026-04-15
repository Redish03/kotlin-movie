package domain.account

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AccountTest {
    @Test
    fun `포인트를 사용하면 그 액수만큼 차감된다`() {
        val account = Account(Point(2000))

        account.useMyPoint(2000)
        assertEquals(0, account.point.amount)
    }
}
