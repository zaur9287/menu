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
                 status        : String,
                 opened        : Option[DateTime],
                 submitted     : Option[DateTime]
               ){
  def toUrl:Int = id
}

object SMS {

  implicit val jsonFormat = Json.format[SMS]

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

  case class UnsentMessages (
                            quizID:Int,
                            quizName:String,
                            categoryID:Int,
                            categoryName:String,
                            status:Boolean
                            )
  object UnsentMessages {
    implicit val jsonFormat = Json.format[UnsentMessages]
  }
}

