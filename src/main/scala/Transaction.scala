import java.time.{LocalDate, LocalDateTime}

case class Transaction(
  timestamp:     LocalDateTime,
  productName:   String,
  expiryDate:    LocalDate,
  quantity:      Int,
  unitPrice:     Double,
  channel:       String,
  paymentMethod: String
)

case class ProcessedTransaction(
  timestamp:     LocalDateTime,
  productName:   String,
  expiryDate:    LocalDate,
  quantity:      Int,
  unitPrice:     Double,
  channel:       String,
  paymentMethod: String,
  discount:      Double,
  finalPrice:    Double
)
