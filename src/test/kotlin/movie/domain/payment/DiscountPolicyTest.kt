package movie.domain.payment

import movie.domain.payment.discount.MovieDayDiscountPolicy
import movie.domain.payment.discount.TimeDiscountPolicy
import movie.domain.reservation.ReservedScreen
import movie.domain.screening.Movie
import movie.domain.screening.MovieTitle
import movie.domain.screening.RunningTime
import movie.domain.screening.Screening
import movie.domain.screening.ScreeningStartTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DiscountPolicyTest {
    private fun createReservedScreen(dateTime: LocalDateTime): ReservedScreen {
        val movie =
            Movie(
                title = MovieTitle("테스트 영화"),
                runningTime = RunningTime(120),
            )
        val screening =
            Screening.create(
                movie = movie,
                startTime = ScreeningStartTime(dateTime),
                reservedSeats = emptyList(),
            )
        return ReservedScreen(
            screen = screening,
            seats = emptyList(),
        )
    }

    @Test
    fun `10일, 20일, 30일은 10% 무비데이 할인이 적용된다`() {
        val discountPolicy = MovieDayDiscountPolicy()
        val totalAmount = 10000
        val expectedAmount = 9000

        // 1월 10일
        val reservedScreen10 = createReservedScreen(LocalDateTime.of(2026, 1, 10, 14, 0))
        assertEquals(expectedAmount, discountPolicy.discount(reservedScreen10, totalAmount))

        // 1월 20일
        val reservedScreen20 = createReservedScreen(LocalDateTime.of(2026, 1, 20, 14, 0))
        assertEquals(expectedAmount, discountPolicy.discount(reservedScreen20, totalAmount))
    }

    @Test
    fun `무비데이(10, 20, 30일)가 아니면 할인이 적용되지 않는다`() {
        val discountPolicy = MovieDayDiscountPolicy()
        val totalAmount = 10000

        // 1월 9일
        val reservedScreen9 = createReservedScreen(LocalDateTime.of(2026, 1, 9, 14, 0))
        assertEquals(totalAmount, discountPolicy.discount(reservedScreen9, totalAmount))
    }

    @Test
    fun `11시 이전 또는 20시 이후 영화는 2000원 정액 할인이 적용된다`() {
        val discountPolicy = TimeDiscountPolicy()
        val totalAmount = 10000
        val expectedAmount = 8000

        // 오전 10시 (11시 이전)
        val morningScreen = createReservedScreen(LocalDateTime.of(2026, 1, 1, 10, 0))
        assertEquals(expectedAmount, discountPolicy.discount(morningScreen, totalAmount))

        // 오후 8시 (20시 이후 포함)
        val nightScreen = createReservedScreen(LocalDateTime.of(2026, 1, 1, 20, 0))
        assertEquals(expectedAmount, discountPolicy.discount(nightScreen, totalAmount))
    }

    @Test
    fun `11시 ~ 19시 사이 영화는 할인이 적용되지 않는다`() {
        val discountPolicy = TimeDiscountPolicy()
        val totalAmount = 10000

        // 오후 2시
        val normalScreen = createReservedScreen(LocalDateTime.of(2026, 1, 1, 14, 0))
        assertEquals(totalAmount, discountPolicy.discount(normalScreen, totalAmount))
    }
}
