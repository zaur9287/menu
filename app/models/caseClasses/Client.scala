package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json

case class Client(
                 id: Int,
                 name: String,
                 description: Option[String],
                 expireDate: DateTime,
                 createdAt: DateTime,
                 updatedAt: DateTime
                 )

object Client {
  import play.api.libs.json.JodaWrites._
  import play.api.libs.json.JodaReads._

  implicit val jsonFormat = Json.format[Client]

  val form = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> optional(nonEmptyText),
      "expireDate" -> text
    )
  )
}




object  ClientForms {

  // client update form
  case class UpdateClientForm(
                               name: Option[String],
                               description: Option[String],
                               expireDate: Option[DateTime]
                             ){
    def tupled = (name, description, expireDate)
  }

  object UpdateClientForm {
    implicit val jsonFormat = Json.format[UpdateClientForm]
  }

  val updateForm = Form(
    mapping(
      "name" -> optional(nonEmptyText),
      "description" -> optional(nonEmptyText),
      "expireDate" -> optional(nonEmptyText).transform(s => s.map(ds => DateTime.parse(ds)), (d: DateTime) => Some(d.toString))
    )(UpdateClientForm.apply)(UpdateClientForm.unapply)
  )

}