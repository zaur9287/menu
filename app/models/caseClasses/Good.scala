package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form


case class Good(
                       groupID: Int,
                       companyID: Int,
                       name: String,
                       description: Option[String],
                       price: Double,
                       quantity: Double,
                       imageID: Int,
                       createdAt: DateTime,
                       updatedAt: DateTime,
                      )

object Good {
  implicit val jsonFormat = Json.format[Good]

//  val form = Form(mapping(
//    "groupID" -> number,
//    "companyID" -> number,
//    "name" -> nonEmptyText,
//    "description" -> optional(nonEmptyText),
//    "price" -> double,
//    "quantity" -> double,
//    "imageID" -> number
//  ))


}