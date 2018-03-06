package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json

case class Client(
                 id: Int,
                 name: String,
                 description: Option[String],
                 expireDate: DateTime,
                 createdAt: DateTime,
                 updatedAt: DateTime
                 )

object Client {
  import play.api.libs.json.JodaWrites._
  import play.api.libs.json.JodaReads._

  implicit val jsonFormat = Json.format[Client]

  val form = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> optional(nonEmptyText),
      "expireDate" -> text
    )
  )
}