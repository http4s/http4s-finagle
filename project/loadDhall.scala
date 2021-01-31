import us.oyanglul.dhall.generic._
import org.dhallj.syntax._
import org.dhallj.codec.syntax._
import org.dhallj.codec.Decoder._


case class Config(
  http4sVersion: String,
  finagleVersion: String,
  version: String
)
object dhall {
  lazy val config = {
    val Right(decoded) = "./app.dhall".parseExpr.flatMap(_.resolve).flatMap(_.normalize().as[Config])
    decoded
  }
}
