package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form
import play.api.data.format.Formats._


case class OrderDetailForm(
                  goodID: Int,
                  price: Double,
                  quantity: Double
                )

object OrderDetailForm {
  implicit val jsonFormat = Json.format[OrderDetailForm]

  val formMapping = mapping(
      "goodID" -> number,
      "price" -> of[Double],
      "quantity" -> of[Double]
    )(OrderDetailForm.apply)(OrderDetailForm.unapply)
  val form = Form(formMapping)
}

case class OrderDetailView(
                          orderID: Int,
                          good: Good,
                          price: Double,
                          quantity: Double,
                          amount: Double
                          )

object OrderDetailView{
  implicit val jsonFormat = Json.format[OrderDetailView]
}
