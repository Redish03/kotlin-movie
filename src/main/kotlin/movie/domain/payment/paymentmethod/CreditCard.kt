package movie.domain.payment.paymentmethod

class CreditCard : PaymentMethod() {
    override val rate: Double
        get() = 0.05
}
