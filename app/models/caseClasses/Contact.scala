package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form

case class Contact (
                     id: Int,
                     property: String,
                     value: String,
                     userID: Option[String],
                     companyID: Option[Int],
                     createdAt: DateTime,
                     updatedAt: DateTime
                   )

object Contact {
  implicit val jsonFormat = Json.format[Contact]
}

case class ContactForm (
                         property: String,
                         value: String,
                         userID: Option[String],
                         companyID: Option[Int]
                       )
object ContactForm {
  implicit val jsonFormat = Json.format[ContactForm]

  val formMapping = mapping (
    "property" -> nonEmptyText,
    "value" -> nonEmptyText,
    "userID" -> optional(nonEmptyText),
    "companyID" -> optional(number)
  )(ContactForm.apply)(ContactForm.unapply)

  val form = Form(formMapping)
}

case class ContactFilterForm(
                              userID: Option[String],
                              companyID: Option[Int]
                            )
object ContactFilterForm{
  implicit val jsonFormat = Json.format[ContactFilterForm]
  val form = Form(mapping (
    "userID" -> optional(nonEmptyText),
    "companyID" -> optional(number)
  )(ContactFilterForm.apply)(ContactFilterForm.unapply)
  )
}