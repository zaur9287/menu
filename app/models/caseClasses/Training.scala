package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Training(id         : Int,
                    name       : String,
                    createdAt  : DateTime,
                    updatedAt  : DateTime,
                    deletedAt  : Option[DateTime])

object Training {

  implicit val jsonFormat = Json.format[Training]

  case class UpdateFormTraining(name:String)

  object UpdateFormTraining { implicit val jsonFormat = Json.format[UpdateFormTraining]  }

  val updateForm = Form(
    mapping("name" -> nonEmptyText)(UpdateFormTraining.apply)(UpdateFormTraining.unapply)
  )

  case class CreateFormTraining(
                         name: String
                       )

  object CreateFormTraining{implicit val jsonFormat = Json.format[CreateFormTraining]}
  val form= Form(
    mapping(
      "name"       -> nonEmptyText
    )(CreateFormTraining.apply)(CreateFormTraining.unapply)
  )
}

