package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._


case class Participant(
                 id         : Int,
                 name       : String,
                 phone      : String,
                 company    : String,
                 categoryID : Int,
                 createdAt  : DateTime,
                 updatedAt  : DateTime,
                 deletedAt  : Option[DateTime]
                 )

object Participant {

  implicit val jsonFormat = Json.format[Participant]

  case class UpdateFormParticipant(
                                    name       : String,
                                    phone      : String,
                                    company    : String,
                                    categoryID : Int
                             )

  object UpdateFormParticipant { implicit val jsonFormat = Json.format[UpdateFormParticipant] }

  val updateForm = Form(
    mapping(
      "name"       -> nonEmptyText,
      "phone"      -> nonEmptyText,
      "company"    -> text,
      "category_id"-> number
    )(UpdateFormParticipant.apply)(UpdateFormParticipant.unapply)
  )

  case class CreateFormParticipant(
                                    name       : String,
                                    phone      : String,
                                    company    : String,
                                    categoryID : Int
                             )

  object CreateFormParticipant{implicit val jsonFormat = Json.format[CreateFormParticipant]}
  val form= Form(
    mapping(
      "name"       -> nonEmptyText,
      "phone"      -> nonEmptyText,
      "company"    -> text,
      "category_id"-> number
    )(CreateFormParticipant.apply)(CreateFormParticipant.unapply)
  )
}

