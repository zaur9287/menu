package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class SMS(
                 id            : Int,
                 participantID : Int,
                 trainingID    : Int,
                 categoryID    : Int,
                 quizID        : Int,
                 sent          : Option[DateTime],
                 opened        : Option[DateTime],
                 submitted     : Option[DateTime]
               ){
  def toUrl:Int = id
}

object SMS {

  implicit val jsonFormat = Json.format[SMS]

  case class UpdateFormSMS(
                            participantID : Int,
                            trainingID    : Int,
                            categoryID    : Int,
                            quizID        : Int,
                            sent          : Option[String],
                            opened        : Option[String],
                            submitted     : Option[String]
                           )

  object UpdateFormSMS { implicit val jsonFormat = Json.format[UpdateFormSMS] }

  val updateForm = Form(
    mapping(
      "participantID" -> number,
      "trainingID"    -> number,
      "categoryID"    -> number,
      "quizID"        -> number,
      "sent"          -> optional (text),//.transform(s => s.map(ds => DateTime.parse(ds)), (d: DateTime) => Some(d.toString)),
      "opened"        -> optional (text),//.transform(s => s.map(ds => DateTime.parse(ds)), (d: DateTime) => Some(d.toString)),
      "submitted"     -> optional (text)//.transform(s => s.map(ds => DateTime.parse(ds)), (d: DateTime) => Some(d.toString))
    )(UpdateFormSMS.apply)(UpdateFormSMS.unapply)
  )

  case class SentFormSMS(
                            categoryID    : Int,
                            quizID        : Int
                           )

  object SentFormSMS{implicit val jsonFormat = Json.format[SentFormSMS]}
  val sentForm= Form(
    mapping(
      "categoryID"    -> number,
      "quizID"        -> number
    )(SentFormSMS.apply)(SentFormSMS.unapply)
  )
}

