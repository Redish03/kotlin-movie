package movie.domain.payment.paymentmethod

class Cash : PaymentMethod() {
    override val rate: Double
        get() = 0.02
}
