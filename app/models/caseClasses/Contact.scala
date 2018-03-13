package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json

case class Contact(
                 id: Int,
                 client_id: Int,
                 mobile: String,
                 description: String,
                 createdAt: DateTime,
                 updatedAt: DateTime
                 )

object Contact {
  import play.api.libs.json.JodaWrites._
  import play.api.libs.json.JodaReads._

  implicit val jsonFormat = Json.format[Contact]

  val form = Form(
    tuple(
      "client_id" -> number,
      "mobile" -> nonEmptyText,
      "description" -> nonEmptyText,
    )
  )
}




object  ContactForms {

  // client contact update form
  case class UpdateContactForm(
                               client_id:Int,
                               mobile: String,
                               description: String
                             ){
    def tupled = (client_id,mobile, description)
  }

  object UpdateClientForm {
    implicit val jsonFormat = Json.format[UpdateContactForm]
  }

  val updateForm = Form(
    mapping(
      "client_id"    -> number,
      "number"       -> text,
      "description"  -> text //.transform(s => s.map(ds => DateTime.parse(ds)), (d: DateTime) => Some(d.toString))
    )(UpdateContactForm.apply)(UpdateContactForm.unapply)
  )

}