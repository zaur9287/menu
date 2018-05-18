package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form


case class Order(
                  tableID: Int,
                  companyID: Int,
                  userID: String,
                  goodID: Int,
                  price: Double,
                  quantity: Double,
                  status: Option[String],
                  createdAt: DateTime,
                  updatedAt: DateTime,
                )

object Order {
  implicit val jsonFormat = Json.format[Order]

//  val form = Form(mapping(
//    "tableID" -> number,
//    "companyID" -> number,
//    "userID" -> number,
//    "goodID" -> number,
//    "price" -> double,
//    "quantity" -> double,
//    "status" -> optional(nonEmptyText)
//  ))


}