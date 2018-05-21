package models.caseClasses


import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form


case class Company (
                     id: Int,
                     name: String,
                     description: Option[String],
                     imageID: Int,
                     createdAt: DateTime,
                     updatedAt: DateTime
                   )


object Company {
  implicit val jsonFormat = Json.format[Company]

}

case class CompanyForm(
                      name: String,
                      description: Option[String],
                      imageID: Int
                      )
object CompanyForm {
  implicit val jsonFormat = Json.format[CompanyForm]
  val formMapping = mapping(
    "name" -> nonEmptyText,
    "description" -> optional(nonEmptyText),
    "imageID" -> number
  )(CompanyForm.apply)(CompanyForm.unapply)

  val form = Form(formMapping)
}