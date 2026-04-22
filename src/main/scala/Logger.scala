import java.io.{FileWriter, PrintWriter}
import java.time.LocalDateTime
import scala.util.Try

object Logger {

  private val logFile = "rules_engine.log"

  private def log(level: String, message: String): Try[Unit] = Try {
    val writer    = new PrintWriter(new FileWriter(logFile, true))
    val timestamp = LocalDateTime.now()
    writer.println(s"$timestamp $level $message")
    writer.close()
  }

  def info(message: String):  Try[Unit] = log("INFO ", message)
  def warn(message: String):  Try[Unit] = log("WARN ", message)
  def error(message: String): Try[Unit] = log("ERROR", message)
}
