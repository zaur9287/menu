package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json

case class Country(
                 id: Int,
                 name: String,
                 description: Option[String],
                 createdAt: DateTime,
                 updatedAt: DateTime
                 )

object Country {
  import play.api.libs.json.JodaWrites._
  import play.api.libs.json.JodaReads._

  implicit val jsonFormat = Json.format[Country]

  val form = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> optional(nonEmptyText),
    )
  )
}

object  CountryForms {

  // Country update form
  case class UpdateCountryForm(
                               name:String,
                               description: String
                             ){
    def tupled = (name, description)
  }

  object UpdateCountryForm {
    implicit val jsonFormat = Json.format[UpdateCountryForm]
  }

  val updateForm = Form(
    mapping(
      "name" -> text,
      "description" -> text
    )(UpdateCountryForm.apply)(UpdateCountryForm.unapply)
  )

}