package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form
import play.api.data.format.Formats._



case class Good(
                 id: Int,
                 groupID: Int,
                 companyID: Int,
                 name: String,
                 description: Option[String],
                 price: Double,
                 quantity: Double,
                 imageID: Int,
                 createdAt: DateTime,
                 updatedAt: DateTime,
                )

object Good {
  implicit val jsonFormat = Json.format[Good]
}

case class GoodForm(
                     groupID: Int,
                     companyID: Int,
                     name: String,
                     description: Option[String],
                     price: Double,
                     quantity: Double,
                     imageID: Int
                   )
object GoodForm {
    val formMapping = mapping(
      "groupID" -> number,
      "companyID" -> number,
      "name" -> nonEmptyText,
      "description" -> optional(nonEmptyText),
      "price" -> of[Double],
      "quantity" -> of[Double],
      "imageID" -> number
    )(GoodForm.apply)(GoodForm.unapply)
  val form = Form(formMapping)
}

case class GoodFilterForm(
                           groupID: Option[Int],
                           companyID: Option[Int],
                         )
object GoodFilterForm {
  implicit val jsonFormat = Json.format[GoodFilterForm]
  val form = Form(
    mapping(
      "groupID" -> optional(number),
      "companyID" -> optional(number)
    )(GoodFilterForm.apply)(GoodFilterForm.unapply)
  )
}