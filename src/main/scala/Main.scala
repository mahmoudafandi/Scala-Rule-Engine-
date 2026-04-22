import com.github.tototoshi.csv.CSVReader
import java.io.File
import java.time.{LocalDate, LocalDateTime}
import scala.util.{Success, Failure}
import scala.collection.parallel.CollectionConverters._

object Main extends App {

  Logger.info("Starting rule engine")

  val reader = CSVReader.open(new File("resources/TRX1000.csv"))

  // Stream the file in batches of 10,000 instead of loading all at once
  val batchSize = 10000

  reader.iteratorWithHeaders()
    .grouped(batchSize)
    .zipWithIndex
    .foreach { case (batch, batchIndex) =>

      Logger.info(s"Processing batch ${batchIndex + 1} (${batch.size} rows)")

      // Parse each row into a Transaction
      val transactions = batch.map { row =>
        Transaction(
          timestamp     = LocalDateTime.parse(row("timestamp")),
          productName   = row("product_name"),
          expiryDate    = LocalDate.parse(row("expiry_date")),
          quantity      = row("quantity").toInt,
          unitPrice     = row("unit_price").toDouble,
          channel       = row("channel"),
          paymentMethod = row("payment_method")
        )
      }

      // Process in parallel across all CPU cores
      val processed = transactions.par.map(RuleEngine.calculateFinalPrice).toList

      // Write batch to DB
      DatabaseWriter.writeToDatabase(processed) match {
        case Success(_)  => Logger.info(s"Batch ${batchIndex + 1} written successfully")
        case Failure(ex) => Logger.error(s"Batch ${batchIndex + 1} failed: ${ex.getMessage}")
      }
    }

  reader.close()
  Logger.info("Rule engine finished")
}
