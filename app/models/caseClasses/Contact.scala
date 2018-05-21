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
                     companyID: Int,
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
                         companyID: Int
                       )
object ContactForm {
  implicit val jsonFormat = Json.format[ContactForm]

  val formMapping = mapping (
    "property" -> nonEmptyText,
    "value" -> nonEmptyText,
    "userID" -> optional(nonEmptyText),
    "companyID" -> number
  )(ContactForm.apply)(ContactForm.unapply)

  val form = Form(formMapping)
}
