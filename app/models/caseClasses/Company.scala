package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json

case class Company(
                 id: Int,
                 name: String,
                 description: Option[String],
                 createdAt: DateTime,
                 updatedAt: DateTime
                 )

object Company {
  import play.api.libs.json.JodaWrites._
  import play.api.libs.json.JodaReads._

  implicit val jsonFormat = Json.format[Company]

  val form = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> optional(nonEmptyText),
    )
  )
}

object  CompanyForms {

  // company update form
  case class UpdateCompanyForm(
                               name:String,
                               description: String
                             ){
    def tupled = (name, description)
  }

  object UpdateCompanyForm {
    implicit val jsonFormat = Json.format[UpdateCompanyForm]
  }

  val updateForm = Form(
    mapping(
      "name" -> text,
      "description" -> text
    )(UpdateCompanyForm.apply)(UpdateCompanyForm.unapply)
  )

}