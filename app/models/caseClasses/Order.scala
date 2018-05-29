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
                         details: Seq[OrderDetailView]
                       )
object OrderView {
  implicit val jsonFormat = Json.format[OrderView]
}

case class OrderSimple(
                        id: Int,
                        table: Table_,
                        company: Company,
                        user: User,
                        status: Option[String],
                        createdAt: DateTime,
                        updatedAt: DateTime
                      )
object OrderSimple{ implicit val jsonFormat = Json.format[OrderSimple] }


case class OrderForm (
                     tableID: Int,
                     companyID: Int,
                     userID: String,
                     status: Option[String],
                     details: Seq[OrderDetailForm]
                     )
object OrderForm {
  implicit val jsonFormat = Json.format[OrderForm]

  val formMapping = mapping(
    "tableID" -> number,
    "companyID" -> number,
    "userID" -> nonEmptyText,
    "status" -> optional(nonEmptyText),
    "details" -> seq(OrderDetailForm.formMapping)
  )(OrderForm.apply)(OrderForm.unapply)

  val form = Form(formMapping)
}

case class OrderFilterForm(
                            tableID: Option[Int],
                            companyID: Option[Int],
                            userID: Option[String],
                            status: Option[String],
                            goodID: Option[Int]
                          )
object OrderFilterForm{
  implicit val jsonFormat = Json.format[OrderFilterForm]

  val form = Form(
    mapping(
      "tableID" -> optional(number),
      "companyID" -> optional(number),
      "userID" -> optional(nonEmptyText),
      "status" -> optional(nonEmptyText),
      "goodID" -> optional(number)
    )(OrderFilterForm.apply)(OrderFilterForm.unapply)
  )

}