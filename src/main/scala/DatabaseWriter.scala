import java.sql.DriverManager
import scala.util.Try

object DatabaseWriter {

  def writeToDatabase(transactions: List[ProcessedTransaction]): Try[Unit] = Try {
    val url        = "jdbc:mysql://localhost:3306/your_database_name"
    val username   = "root"
    val password   = "yourpassword"

    val connection = DriverManager.getConnection(url, username, password)

    // Disable auto-commit so we control when the batch is flushed
    connection.setAutoCommit(false)

    val sql = """
      INSERT INTO RuleEngine
        (timestamp, productName, expiryDate, quantity, unitPrice, channel, paymentMethod, discount, finalPrice)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    val statement = connection.prepareStatement(sql)

    transactions.foreach { t =>
      statement.setString(1, t.timestamp.toString)
      statement.setString(2, t.productName)
      statement.setString(3, t.expiryDate.toString)
      statement.setInt   (4, t.quantity)
      statement.setDouble(5, t.unitPrice)
      statement.setString(6, t.channel)
      statement.setString(7, t.paymentMethod)
      statement.setDouble(8, t.discount)
      statement.setDouble(9, t.finalPrice)
      statement.addBatch()    // queue the row instead of sending immediately
    }

    statement.executeBatch()  // send all queued rows to DB in one shot
    connection.commit()       // confirm the transaction
    connection.close()
  }
}
