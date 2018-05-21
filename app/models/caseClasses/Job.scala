package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form

case class Job(
                id: Int,
                userID: String,
                companyID: Int,
                roleID: Int,
                name: String,
                description: Option[String],
                createdAt: DateTime,
                updatedAt: DateTime
              )

object Job {
  implicit val jsonFormat = Json.format[Job]
}

case class JobForm (
                     userID: String,
                     companyID: Int,
                     roleID: Int,
                     name: String,
                     description: Option[String]
                   )
object JobForm {
  implicit val jsonFormat = Json.format[JobForm]
  val formMapping = mapping(
    "userID" -> nonEmptyText,
    "companyID" -> number,
    "roleID" -> number,
    "name" -> nonEmptyText,
    "description" -> optional(nonEmptyText)
  )(JobForm.apply)(JobForm.unapply)
  val form = Form(formMapping)
}