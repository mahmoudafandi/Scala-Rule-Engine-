import java.time.temporal.ChronoUnit

object RuleEngine {

  // Rule 1 — Expiry-based discount
  // 29 days left = 1%, 28 days = 2%, ..., 1 day left = 29%
  def expiryDiscount(t: Transaction): Option[Double] = {
    val today         = t.timestamp.toLocalDate
    val daysRemaining = ChronoUnit.DAYS.between(today, t.expiryDate)

    if (daysRemaining < 30) Some((30 - daysRemaining) / 100.0)
    else None
  }

  // Rule 2 — Product type discount
  // Cheese = 10%, Wine = 5%
  def productDiscount(t: Transaction): Option[Double] = {
    val name = t.productName.toLowerCase
    if      (name.contains("cheese")) Some(0.10)
    else if (name.contains("wine"))   Some(0.05)
    else                              None
  }

  // Rule 3 — Special date discount
  // March 23rd = 50%
  def specialDateDiscount(t: Transaction): Option[Double] = {
    if (t.timestamp.getMonthValue == 3 && t.timestamp.getDayOfMonth == 23) Some(0.50)
    else None
  }

  // Rule 4 — Quantity discount
  // 6-9 units = 5%, 10-14 units = 7%, 15+ units = 10%
  def quantityDiscount(t: Transaction): Option[Double] = {
    if      (t.quantity >= 6  && t.quantity <= 9)  Some(0.05)
    else if (t.quantity >= 10 && t.quantity <= 14) Some(0.07)
    else if (t.quantity >= 15)                     Some(0.10)
    else                                           None
  }

  // Rule 5 — App discount
  // Sales through App: quantity rounded up to nearest multiple of 5, then / 100
  def appDiscount(t: Transaction): Option[Double] = {
    if (t.channel.toLowerCase == "app") Some(Math.ceil(t.quantity / 5.0) * 5 / 100.0)
    else None
  }

  // Rule 6 — Visa discount
  // Visa card payments = 5%
  def visaDiscount(t: Transaction): Option[Double] = {
    if (t.paymentMethod.toLowerCase == "visa") Some(0.05)
    else None
  }

  // Aggregation — run all rules, take top 2, average them
  def applyDiscounts(t: Transaction): Double = {
    val allDiscounts = List(
      expiryDiscount(t),
      productDiscount(t),
      specialDateDiscount(t),
      quantityDiscount(t),
      appDiscount(t),
      visaDiscount(t)
    ).flatten

    if (allDiscounts.isEmpty) 0.0
    else {
      val top2    = allDiscounts.sortBy(-_).take(2)
      val average = top2.sum / top2.length
      average
    }
  }

  // Final price calculation
  def calculateFinalPrice(t: Transaction): ProcessedTransaction = {
    val discount   = applyDiscounts(t)
    val finalPrice = t.unitPrice * t.quantity * (1 - discount)

    ProcessedTransaction(
      t.timestamp,
      t.productName,
      t.expiryDate,
      t.quantity,
      t.unitPrice,
      t.channel,
      t.paymentMethod,
      discount,
      finalPrice
    )
  }
}
