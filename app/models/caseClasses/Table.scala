package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form


case class Table_(
                  companyID: Int,
                  name: String,
                  description: Option[String],
                  imageID: Int,
                  createdAt: DateTime,
                  updatedAt: DateTime,
               )

object Table_ {
  implicit val jsonFormat = Json.format[Table_]

//  val form = Form(mapping(
//    "companyID" -> number,
//    "name" -> nonEmptyText,
//    "description" -> optional(nonEmptyText),
//    "imageID" -> number
//  ))


}