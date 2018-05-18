package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form


case class Image(
                path: String,
                createdAt: DateTime,
                updatedAt: DateTime,
                )

object Image {
  implicit val jsonFormat = Json.format[Image]

//  val form = Form(mapping(
//    "path" -> nonEmptyText
//  ))


}