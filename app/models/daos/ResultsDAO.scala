package models.daos
import org.joda.time.{DateTime, Seconds}
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Result
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ResultsDAOImpl])
trait ResultsDAO {
  def create(row: Result): Future[Int]
  def createMultiply(rows: Seq[Result]): Future[Int]
  def delete(selectedID:Int): Future[Int]
  def findByID(id: Int): Future[Option[Result]]
  def allReport(tID:Option[Int],cID:Option[Int],qID:Option[Int]):Future[Int]//training category quiz
}

class ResultsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends ResultsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._


  override def create(row: Result): Future[Int] = {
    for {
      correct<-db.run(slickAnswers.filter(f=>f.deletedAt.isEmpty && f.correct === true && f.questionID === row.questionID ).result.head.map(_.correct))
      weight<-db.run(slickQuestions.filter(f=>f.deletedAt.isEmpty && f.id === row.questionID).result.head.map(_.weight))
      createdAt <- db.run(slickSMS.filter(f => f.id === row.SMSID && f.opened.isDefined).result.headOption)
      affected<- {
        val responseTime = createdAt.map ( sms =>
          sms.opened.map( openedDefined => Seconds.secondsBetween(openedDefined, sms.submitted.get).getSeconds)
        ).flatten.getOrElse(0)
        db.run(slickResult+=DBResult(row.id, row.SMSID,row.questionID,row.answerID, correct, weight,responseTime))
      }
    }yield{
      affected
    }
//    val dBRow = DBResult(row.id, row.SMSID,row.questionID,row.answerID, row.correct,row.weight)
//    val query = slickResult.returning(slickResult) += dBRow
//    db.run(query).map ( r => Some(r.toResult) )
  }
  override def createMultiply(rows: Seq[Result]): Future[Int]= {
    val dbResults = rows.map( r=> DBResult(0,r.SMSID,r.questionID,r.answerID,r.correct,r.weight,0))

    val result = db.run(slickResult ++= dbResults).map( r =>
      rows.length)
    result
  }

  override def delete(selectedID:Int): Future[Int] = {
    val selectedClient = slickResult.filter(_.id===selectedID)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def findByID(id:Int): Future[Option[Result]] = {
    val query = slickResult.filter(f=>f.id === id ).result
    db.run(query.headOption).map(_.map(_.toResult))
  }


  override def allReport(tID:Option[Int],cID:Option[Int],qID:Option[Int]):Future[Int] = {
    val query =   slickResult.filter(f=>f.correct===true)
                  .join(slickSMS          .filter(f=>f.submitted.isDefined))  .on(_.SMSID === _.id)
                  .join(slickParticipants .filter(f=>f.deletedAt.isEmpty))    .on(_._2.participantID === _.id)
                  .join(slickCategories   .filter(f=>f.deletedAt.isEmpty))    .on(_._1._2.categoryID === _.id)
                  .join(slickQuizzes      .filter(f=>f.deletedAt.isEmpty))    .on(_._1._1._2.quizID === _.id)
                  .join(slickTrainings    .filter(f=>f.deletedAt.isEmpty))    .on(_._1._1._1._2.trainingID===_.id)
    val testQuery = for {
      cols<-slickResult.filter(f=>f.correct===true)
        .join(slickSMS          .filter(f=>f.submitted.isDefined))  .on(_.SMSID === _.id)
        .join(slickParticipants .filter(f=>f.deletedAt.isEmpty))    .on(_._2.participantID === _.id)
        .join(slickCategories   .filter(f=>f.deletedAt.isEmpty))    .on(_._1._2.categoryID === _.id)
        .join(slickQuizzes      .filter(f=>f.deletedAt.isEmpty))    .on(_._1._1._2.quizID === _.id)
        .join(slickTrainings    .filter(f=>f.deletedAt.isEmpty))    .on(_._1._1._1._2.trainingID===_.id)

    } yield{
      (cols._1._1._1._2.name,
        cols._1._1._2.name,
        cols._2.name,cols._1._2.name,
        cols._1._1._1._1._1.weight,
        cols._1._1._1._1._1.correct,
        cols._1._1._1._1._1.response
      )
    }

    val tt = db.run(testQuery.groupBy(r=>r._1).result)
    //tt
    Future(0)

  }

}


