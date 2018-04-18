package models.daos
import java.lang.ProcessBuilder.Redirect
import java.util.UUID

import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses._
import models.caseClasses.SMS._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import utils.{GateWaySMS, Hashids, SMSApi}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[SMSDAOImpl])
trait SMSDAO {
  def createRows(cID:Int,qID:Int)             : Future[Int]
  def delete(id:Int)                          : Future[Int]
  def updateOpened(id:Int)                    : Future[Int]
  def findUnSubmitted(id:Int)                 : Future[Boolean]
  def sendSMS                                 : Future[Option[Int]]
  def getQuiz(id:String)                      : Future[Option[TestModel]]
  def updateSubmit(id:Int)                    : Future[Int]
  def unsentMessages                          : Future[Seq[UnsentMessages]]
}

class SMSDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,wSClient: WSClient)(implicit executionContext: ExecutionContext)  extends SMSDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._


  override def createRows(cID:Int,qID:Int): Future[Int] = {
    val participantQuery = slickParticipants.filter(f => f.deletedAt.isEmpty && f.categoryID === cID)
      .join(slickSMS.filter(f=>f.categoryID === cID && f.quizID === qID)).on(_.id === _.participantID)

    val any = for {
      un<-db.run(participantQuery.result).map(r=>r.length)
      trainingID <- db.run(slickQuizzes.filter(f=>f.categoryID === cID && f.id === qID ).map(_.trainingID).result.headOption)
    }yield{
      if(un ==0 && trainingID.getOrElse(0)>0){
        val mytest  = for {
          temp <- db.run(slickParticipants.filter(f => f.deletedAt.isEmpty && f.categoryID === cID).result).map(_.map(participant =>
            DBSMS(0, participant.id, trainingID.get, cID, qID, "pending", None, None)))
        }yield {db.run(slickSMS++=temp).map(_.map(r=>r))}
        mytest.flatten
      }else{Future(None)}
    }
    any.flatten.map(_.getOrElse(0))

  }

  override def delete(id:Int): Future[Int] = {
    val selectedClient = slickSMS.filter(_.id===id)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override  def updateOpened(id:Int): Future[Int] = {

    val updateQuery = slickSMS.filter(c => c.id === id)
      .map(c => c.opened)
      .update(Some(DateTime.now()))
      db.run(updateQuery)
  }

  override def findUnSubmitted(id:Int): Future[Boolean] = {
    val query = slickSMS.filter(f=>f.id === id && f.submitted.isEmpty).result.headOption.map(_.map(_.submitted.isEmpty).getOrElse(false))
    db.run(query)
  }

  override def sendSMS: Future[Option[Int]] = {
    println("sendSMS function is working...")
    val hashids = new Hashids
    val smsApi = new SMSApi(wSClient)

    val q = slickSMS.filter(f=>f.status === "pending")
      .joinLeft(slickParticipants.filter(_.deletedAt.isEmpty)).on(_.participantID ===_.id).take(30)

    for {
      seq<-db.run(q.result)
      smsIDs <- {
        var recipients: Seq[GateWaySMS] = Seq()
        seq.foreach(r => {
          val (sms, participant) = r
          if (participant.isDefined) {
            recipients = recipients :+ GateWaySMS (participant.get.phone, s"Salam ${participant.get.name}. Sizin ucun ayrilmis linke daxil olun http://airp2018.testqmeter.net/quiz/${hashids.encode (sms.id)}")
          }
        })
        for {
          sendAll <- smsApi.sendSms(recipients)
          updateStatuses <- updateStatus( seq.map(_._1.id) ,"sending")
        } yield sendAll
      }
      a <- updateStatus(seq.map(_._1.id), "sent")
    } yield Some(a)
  }


  override def getQuiz(id: String): Future[Option[TestModel]] = {
    val hashids = new Hashids
    val smsApi = new SMSApi(wSClient)
    val smsID = hashids.decode(id)(0).toInt
    def q(sID: Int) = slickSMS.filter(f=>f.id === sID && f.submitted.isEmpty )
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
      updateSMS<-updateOpened(smsID)
    }yield{
      updateSMS // əgər sms submit olunubsa, dəyər göndərilməsin
      r.map { resultSet =>
        val ((((sms, participant), category), training), quiz) = resultSet
        val quests = questionsOption.groupBy(_._1).map( res =>
          TestQuestion(res._1.toQuestion, res._2.map(n => TestAnswer(n._2.id, n._2.text))))
        TestModel(participant.toParticipant,quiz.toQuiz,training.toTraining,category.toCategory,quests.toSeq)
      }
    }
  }

  override  def updateSubmit(id:Int): Future[Int] = {
    val updateQuery = slickSMS.filter(c => c.id === id).map(c => c.submitted).update(Some(DateTime.now))
    db.run(updateQuery)
  }

  private def updateStatus(ids: Seq[Int], status: String): Future[Int] = {
    val updateQuery = slickSMS.filter(c => c.id inSet(ids)).map(_.status).update(status)
    db.run(updateQuery)
  }


  override def unsentMessages: Future[Seq[UnsentMessages]] = {
    val q = slickSMS.join(slickQuizzes.filter(_.deletedAt.isEmpty)).on(_.quizID === _.id)
      .join(slickCategories.filter(_.deletedAt.isEmpty)).on(_._1.categoryID === _.id)
      .result.map(_.map(r => {
      val ((sms, quiz), category) = r
      (quiz.id, quiz.name, category.id, category.name,sms.status!="pending")
    }))
    for{  res<- db.run(q)
    } yield res.map(r=>UnsentMessages(r._1,r._2,r._3,r._4,r._5))
  }
}


