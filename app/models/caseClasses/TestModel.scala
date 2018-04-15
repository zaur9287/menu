package models.caseClasses

import play.api.libs.json.Json



case class TestAnswer(
                       id: Int,
                       text: String
                     )
object TestAnswer {
  implicit val jsonFormat = Json.format[TestAnswer]
}

case class TestQuestion(
                         question: Question,
                         answer: Seq[TestAnswer]
                       )
object TestQuestion {
  implicit val jsonFormat  = Json.format[TestQuestion]
}

case class TestModel (
                     participant: Participant,
                     quiz: Quiz,
                     training: Training,
                     category: Category,
                     questions: Seq[TestQuestion]
                     )
object TestModel {
  implicit val jsonFormat = Json.format[TestModel]
}



