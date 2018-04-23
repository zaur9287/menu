package models.daos
import org.joda.time.{DateTime, Seconds}
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ResultsDAOImpl])
trait ResultsDAO {
  def create(row: Result): Future[Int]
  def createMultiply(rows: Seq[Result]): Future[Int]
  def delete(selectedID:Int): Future[Int]
  def findByID(id: Int): Future[Option[Result]]
  def allReport(tID:Option[Int],cID:Option[Int],qID:Option[Int]):Future[Seq[ReportRow]]
}

class ResultsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends ResultsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._


  override def create(row: Result): Future[Int] = {
    for {
      correct<-db.run(slickAnswers.filter(f=>f.deletedAt.isEmpty && f.id === row.answerID && f.questionID === row.questionID )
        .result.headOption).map(r=>
        if (r.isDefined) r.get.correct
        else false
      )
      weight<-db.run(slickQuestions.filter(f=>f.deletedAt.isEmpty && f.id === row.questionID)
        .result.headOption).map(r=>
        if (r.isDefined) r.get.weight
        else 0
      )
      createdAt <- db.run(slickSMS.filter(f => f.id === row.SMSID && f.opened.isDefined).result.headOption)
      affected<- {
        val responseTime = createdAt.flatMap ( sms =>
          sms.opened.map( openedDefined => Seconds.secondsBetween(openedDefined, sms.submitted.get).getSeconds)
        ).getOrElse(0)
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
    val test = for (
      res<-rows.map(r=>create(r))
    )yield res
    Future.sequence(test).map(_.length)
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


  override def allReport(tID:Option[Int],cID:Option[Int],qID:Option[Int]):Future[Seq[ReportRow]] = {
    var query = slickResult.filter(_.correct === true)
      .join(slickSMS.filter(_.submitted.isDefined)).on(_.SMSID === _.id)
      .join(slickParticipants.filter(_.deletedAt.isEmpty)).on(_._2.participantID === _.id)
      .join(slickCategories.filter(_.deletedAt.isEmpty)).on(_._2.categoryID === _.id)


    tID.foreach(f=>query = query.filter(_._1._1._2.trainingID ===f))
    cID.foreach(f=>query = query.filter(_._1._2.categoryID ===f))
    qID.foreach(f=>query = query.filter(_._1._1._2.quizID     ===f))


    val groupedQuery = query.groupBy( f => {
      val (((result,sms),participant),category) = f
      (participant.id, participant.name, category.id, category.name)
    })

    val selectOnlyQuery = groupedQuery.map( f => {
      val (participantID, participantName, categoryID, categoryName) = f._1
      (participantID, participantName, categoryName, f._2.map(_._1._1._1.weight).sum, f._2.map(_._1._1._1.id).length, f._2.map(_._1._1._1.response).sum)
    }).sortBy(r=>(r._4 , r._6))


    db.run(selectOnlyQuery.result).map(r=>
      r.map(t=>ReportRow(t._1,t._2,t._3,t._4,t._5,t._6))
    )
  }
}


