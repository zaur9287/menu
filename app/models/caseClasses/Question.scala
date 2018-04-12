package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Question(
                 id         : Int,
                 text       : String,
                 weight     : Int,
                 quizID     : Int,
                 createdAt  : DateTime,
                 updatedAt  : DateTime,
                 deletedAt  : Option[DateTime]
               )

object Question {

  implicit val jsonFormat = Json.format[Question]

  case class UpdateFormQuestion(
                               text       :String,
                               weight     :Int,
                               quizID     :Int
                             )

  object UpdateFormQuestion { implicit val jsonFormat = Json.format[UpdateFormQuestion] }

  val updateForm = Form(
    mapping(
      "text"    -> nonEmptyText,
      "weight"  -> number,
      "quiz_id" -> number
    )(UpdateFormQuestion.apply)(UpdateFormQuestion.unapply)
  )

  case class CreateFormQuestion(
                               text       :String,
                               weight     :Int,
                               quizID    :Int
                             )

  object CreateFormQuestion{implicit val jsonFormat = Json.format[CreateFormQuestion]}
  val form= Form(
    mapping(
      "text"    -> nonEmptyText,
      "weight"  -> number,
      "quiz_id" -> number
    )(CreateFormQuestion.apply)(CreateFormQuestion.unapply)
  )
}



