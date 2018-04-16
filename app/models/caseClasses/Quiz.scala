package models.caseClasses

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class Quiz(
                        id         : Int,
                        name       : String,
                        spiker     : String,
                        trainingID : Int,
                        categoryID : Int,
                        createdAt  : DateTime,
                        updatedAt  : DateTime,
                        deletedAt  : Option[DateTime]
                      )

object Quiz {

  implicit val jsonFormat = Json.format[Quiz]

  case class UpdateFormQuiz(
                             name       : String,
                             spiker     : String,
                             trainingID : Int,
                             categoryID : Int
                             )

  object UpdateFormQuiz { implicit val jsonFormat = Json.format[UpdateFormQuiz] }

  val updateForm = Form(
    mapping(
      "name"       -> nonEmptyText,
      "spiker"     -> nonEmptyText,
      "training_id"-> number,
      "category_id"-> number
    )(UpdateFormQuiz.apply)(UpdateFormQuiz.unapply)
  )

  case class CreateFormQuiz(
                             name       : String,
                             spiker     : String,
                             trainingID : Int,
                             categoryID : Int
                             )

  object CreateFormQuiz{implicit val jsonFormat = Json.format[CreateFormQuiz]}
  val form= Form(
    mapping(
      "name"       -> nonEmptyText,
      "spiker"     -> nonEmptyText,
      "training_id"-> number,
      "category_id"-> number
    )(CreateFormQuiz.apply)(CreateFormQuiz.unapply)
  )


  case class SearchFormQuiz (
                            trainingId:Int,
                            categoryId:Int
                            )
  object SearchFormQuiz {
    implicit val jsonFormat = Json.format[SearchFormQuiz]
  }
  val searchForm  = Form(
    mapping(
      "training_id"->number,
      "category_id"->number
    )(SearchFormQuiz.apply)(SearchFormQuiz.unapply)
  )
}

