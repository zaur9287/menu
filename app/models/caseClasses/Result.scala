package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Result(
                id            : Int,
                SMSID         : Int,
                questionID    : Int,
                answerID      : Int,
                correct       : Boolean,
                weight        : Int,
                response      : Int
              )

object Result {

  implicit val jsonFormat = Json.format[Result]

  case class CreateFormResult(
                               questionID    : Int,
                               answerID      : Int
                          )

  object CreateFormResult{
    implicit val jsonFormatResult = Json.format[CreateFormResult]
  }

  val form= Form(
      seq(mapping(
        "question_id" -> number,
        "answer_id"   -> number
      )(CreateFormResult.apply)(CreateFormResult.unapply))
  )

  case class FilterForm(
                       trainingID :Option[Int],
                       categoryID :Option[Int],
                       quizID     :Option[Int]
                       )
  object FilterForm {implicit val jsonFormat = Json.format[FilterForm]}

  val filterForm  = Form(
    mapping (
      "training_id" ->optional(number),
      "category_id" ->optional(number),
      "quiz_id"     ->optional(number)
    )(FilterForm.apply)(FilterForm.unapply)

  )

}


case class ReportRow(
                     participantID:Int,
                     participantName:String,
                     categoryName:String,
                     totalPoint:Option[Int],
                     countCorrect:Int,
                     response:Option[Int])
object ReportRow{
  implicit val jsonFormat = Json.format[ReportRow]
}
