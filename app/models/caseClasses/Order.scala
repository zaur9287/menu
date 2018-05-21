package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form
import play.api.data.format.Formats._


case class Order(
                  id: Int,
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
}

case class OrderForm (
                     tableID: Int,
                     companyID: Int,
                     userID: String,
                     goodID: Int,
                     price: Double,
                     quantity: Double,
                     status: Option[String]
                     )
object OrderForm {

  val formMapping = mapping(
    "tableID" -> number,
    "companyID" -> number,
    "userID" -> nonEmptyText,
    "goodID" -> number,
    "price" -> of[Double],
    "quantity" -> of[Double],
    "status" -> optional(nonEmptyText)
  )(OrderForm.apply)(OrderForm.unapply)

  val form = Form(formMapping)
}