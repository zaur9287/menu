package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json

case class Interface(
                 id: Int,
                 name: String,
                 description: Option[String],
                 createdAt: DateTime,
                 updatedAt: DateTime
                 )

object Interface {
  import play.api.libs.json.JodaWrites._
  import play.api.libs.json.JodaReads._

  implicit val jsonFormat = Json.format[Interface]

  val form = Form(
    tuple(
      "name" -> nonEmptyText,
      "description" -> optional(nonEmptyText)
    )
  )
}




object  InterfaceForms {

  // Interface update form
  case class UpdateInterfaceForm(
                               name:String,
                               description: String
                             ){
    def tupled = (name, description)
  }

  object UpdateInterfaceForm {
    implicit val jsonFormat = Json.format[UpdateInterfaceForm]
  }

  val updateForm = Form(
    mapping(
      "name" -> text,
      "description" -> text
    )(UpdateInterfaceForm.apply)(UpdateInterfaceForm.unapply)
  )

}