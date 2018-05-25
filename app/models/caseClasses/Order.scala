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
                  status: Option[String],
                  createdAt: DateTime,
                  updatedAt: DateTime,
                )

object Order {
  implicit val jsonFormat = Json.format[Order]
}

case class OrderView(
                         id: Int,
                         table: Table_,
                         company: Company,
                         user: User,
                         status: Option[String],
                         createdAt: DateTime,
                         updatedAt: DateTime,
                       )
object OrderView{ implicit val jsonFormat = Json.format[OrderView] }

case class OrderForm (
                     tableID: Int,
                     companyID: Int,
                     userID: String,
                     status: Option[String]
                     )
object OrderForm {

  val formMapping = mapping(
    "tableID" -> number,
    "companyID" -> number,
    "userID" -> nonEmptyText,
    "status" -> optional(nonEmptyText)
  )(OrderForm.apply)(OrderForm.unapply)

  val form = Form(formMapping)
}

case class OrderFilterForm(
                            tableID: Option[Int],
                            companyID: Option[Int],
                            userID: Option[String],
                            status: Option[String]
                          )
object OrderFilterForm{
  implicit val jsonFormat = Json.format[OrderFilterForm]
  val form = Form(
    mapping(
      "tableID" -> optional(number),
      "companyID" -> optional(number),
      "userID" -> optional(nonEmptyText),
      "status" -> optional(nonEmptyText)
    )(OrderFilterForm.apply)(OrderFilterForm.unapply)
  )

}