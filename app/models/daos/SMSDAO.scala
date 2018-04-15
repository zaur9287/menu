package models.daos
import java.lang.ProcessBuilder.Redirect
import java.util.UUID

import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{SMS, TestAnswer, TestModel, TestQuestion}
import models.caseClasses.SMS._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import utils.{GateWaySMS, Hashids, SMSApi}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[SMSDAOImpl])
trait SMSDAO {
  def create(row: SMS)                        : Future[Option[SMS]]
  def createRows(cID:Int,qID:Int)             : Future[Int]
  def delete(id:Int)                          : Future[Int]
  def updateOpened(id:Int)                    : Future[Int]
  def findByID(id: Int)                       : Future[Option[SMS]]
  def findUnSubmitted(id:Int)                 : Future[Boolean]
  def sendSMS                                 : Future[Option[Int]]
  def getQuiz(id:String)                      : Future[Option[TestModel]]
}

class SMSDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,wSClient: WSClient)(implicit executionContext: ExecutionContext)  extends SMSDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._


  override def create(row: SMS): Future[Option[SMS]] = {
    val dBRow = DBSMS(row.id, row.participantID,row.trainingID, row.categoryID, row.quizID,row.sent,row.opened,row.submitted)
    val query = slickSMS.returning(slickSMS) += dBRow
    db.run(query).map ( r => Some(r.toSMS) )
  }

  override def createRows(cID:Int,qID:Int): Future[Int] = {
    val participantQuery = slickParticipants.filter(f => f.deletedAt.isEmpty && f.categoryID === cID)
      .join(slickSMS.filter(f => f.sent.isEmpty && f.categoryID === cID && f.quizID === qID))

    for {
      un<-db.run(participantQuery.result).map(r=>r.length)
    }yield{
      if (un==0){
        for {
          trainingID <- db.run(slickQuizzes.filter(_.id === qID).map(_.trainingID).result.headOption)
          participants <- db.run(participantQuery.result).map(t => t.map(p => DBSMS(0, p._1.id, trainingID.getOrElse(0), cID, qID, None, None, None)))
          affectedRows <- db.run(slickSMS ++= participants).map(_.map(r => r))
        } yield {
          affectedRows.getOrElse(0)
        }
        0
      }else{0}
    }
  }

  override def delete(id:Int): Future[Int] = {
    val selectedClient = slickSMS.filter(_.id===id)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override  def updateOpened(id:Int): Future[Int] = {

    val updateQuery = slickSMS.filter(c => c.id === id)
      .map(c => (c.opened))
      .update((Some(DateTime.now())))
      db.run(updateQuery)
  }

  override def findByID(id:Int): Future[Option[SMS]] = {
    val query = slickSMS.filter(f=>f.id === id).result
    db.run(query.headOption).map(_.map(_.toSMS))
  }

  override def findUnSubmitted(id:Int): Future[Boolean] = {
    val query = slickSMS.filter(f=>f.id === id && f.submitted.isEmpty).result.head.map(_.submitted.isEmpty)
    db.run(query)
  }

  override def sendSMS: Future[Option[Int]] = {
    println("bu funksiya sldksldksld --   == == +")
    val hashids = new Hashids
    val smsApi = new SMSApi(wSClient)
    val query = for {
      (p,s)<- slickParticipants.filter(p=>p.deletedAt.isEmpty)
        .joinRight(slickSMS.filter(s=>s.sent.isEmpty))
        .on((p,s)=>p.id ===s.participantID && p.categoryID === s.categoryID).take(30)
    } yield {
      val tst  = p.map(res=>(res.phone,res.name,s.id))
      tst.getOrElse("","",0)
    }
    for{
        smsBody<-db.run(query.result).map(_.map(row=>
          for {
            sendSMS<-smsApi.sendSms(Seq(GateWaySMS(row._1, s"Salam ${row._2} bu linke daxil olun http://airp2018.testqmeter.net/v1/front/quiz/${hashids.encode(row._3) }")))
            updatedSent<-db.run(slickSMS.filter(c =>c.id === row._3).map(c => (c.sent)).update((Some(DateTime.now()))))
          }yield{
            updatedSent
          }
        ))
    }yield{
      Some(1)
    }
  }


  override def getQuiz(id: String): Future[Option[TestModel]] = {
    val hashids = new Hashids
    val smsApi = new SMSApi(wSClient)
    val smsID = hashids.decode(id)(0).toInt
    def q(sID: Int) = slickSMS.filter(_.id === sID )
      .join(slickParticipants.filter(_.deletedAt.isEmpty)).on(_.participantID===_.id)
      .join(slickCategories.filter(_.deletedAt.isEmpty)).on(_._1.categoryID === _.id)
      .join(slickTrainings.filter(_.deletedAt.isEmpty)).on(_._1._1.trainingID === _.id)
      .join(slickQuizzes.filter(_.deletedAt.isEmpty)).on(_._1._1._1.quizID === _.id)
    for {
      r<-db.run(q(smsID).result.headOption)
      questionsOption <- {
        if(r.isDefined){
          db.run(slickQuestions.filter(k => k.deletedAt.isEmpty && k.quizID === r.get._2.id)
                 .join(slickAnswers.filter(_.deletedAt.isEmpty)).on(_.id === _.questionID).result)
        } else {Future(Seq())}
      }
    }yield{
      r.map { resultSet =>
        val ((((sms, participant), category), training), quiz) = resultSet
        val quests = questionsOption.groupBy(_._1).map( res =>
          TestQuestion(res._1.toQuestion, res._2.map(n => TestAnswer(n._2.id, n._2.text))))
        TestModel(participant.toParticipant,quiz.toQuiz,training.toTraining,category.toCategory,quests.toSeq)
      }
    }
  }

}

