package models.caseClasses

case class TestModel (
                     participant: Participant,
                     quiz: Quiz,
                     training: Training,
                     category: Category,
                     questions: TestQuestion
                     )

case class TestQuestion(
                       question: Question,
                       answer: Seq[TestAnswer]
                       )

case class TestAnswer(
                     id: Int,
                     text: String
                     )
