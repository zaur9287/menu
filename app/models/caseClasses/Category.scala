package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Category(
                     id         : Int,
                     name       : String,
                     createdAt  : DateTime,
                     updatedAt  : DateTime,
                     deletedAt  : Option[DateTime]
                   )

object Category {

  implicit val jsonFormat = Json.format[Category]

  case class UpdateFormCategory(name: String)

  object UpdateFormCategory { implicit val jsonFormat = Json.format[UpdateFormCategory] }

  val updateForm = Form(mapping("name"-> nonEmptyText)(UpdateFormCategory.apply)(UpdateFormCategory.unapply))

  case class CreateFormCategory(name: String)

  object CreateFormCategory{implicit val jsonFormat = Json.format[CreateFormCategory]}
  val form= Form(mapping("name" -> nonEmptyText)(CreateFormCategory.apply)(CreateFormCategory.unapply))

}

