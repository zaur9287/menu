package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Answer(
                 id         : Int,
                 text       : String,
                 correct    : Boolean,
                 questionID : Int,
                 createdAt  : DateTime,
                 updatedAt  : DateTime,
                 deletedAt  : Option[DateTime]
               )

object Answer {

  implicit val jsonFormat = Json.format[Answer]

  case class UpdateFormAnswer(
                       text: String,
                       correct: Boolean,
                       questionID: Int
                       )

  object UpdateFormAnswer { implicit val jsonFormat = Json.format[UpdateFormAnswer] }

  val updateForm = Form(
    mapping(
      "text"       -> nonEmptyText,
      "correct"    -> boolean,
      "question_id"-> number
    )(UpdateFormAnswer.apply)(UpdateFormAnswer.unapply)
  )

  case class CreateFormAnswer(
                         text: String,
                         correct: Boolean,
                         questionID: Int
                       )

  object CreateFormAnswer{implicit val jsonFormat = Json.format[CreateFormAnswer]}
  val form= Form(
    mapping(
      "text"       -> nonEmptyText,
      "correct"    -> boolean,
      "question_id"-> number
    )(CreateFormAnswer.apply)(CreateFormAnswer.unapply)
  )
}



