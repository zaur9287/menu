package models.caseClasses

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json


case class AuthToken(
                  id: UUID,
                  userID: UUID,
                  expiry: DateTime
                 )extends Identity

object AuthToken {
  import play.api.libs.json.JodaWrites._
  import play.api.libs.json.JodaReads._

  implicit val jsonFormat = Json.format[AuthToken]


}


