package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form


case class Role(
               name: String,
               createdAt: DateTime,
               updatedAt: DateTime,
               )

object Role {
  implicit val jsonFormat = Json.format[Role]

//  val form = Form(mapping(
//    "name" -> nonEmptyText
//  ))


}