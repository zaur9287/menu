package models.daos
import java.lang.ProcessBuilder.Redirect
import java.util.UUID

import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Result
import models.caseClasses._
import models.caseClasses.SMS._
import net.minidev.asm.ex.ConvertException
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
  def getParticipantLog(id:Int)               : Future[Option[ParticipantLog]]
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

    val updateQuery = slickSMS.filter(c => c.id === id && c.opened.isEmpty)
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
          updateStatuses <- updateStatus( seq.map(_._1.id) ,"sending")
          sendAll <- smsApi.sendSms(recipients)
        } yield sendAll
      }
      a <- updateStatus(seq.map(_._1.id), "sent")
    } yield Some(a)
  }


  override def getQuiz(id: String): Future[Option[TestModel]] = {
    val hashids = new Hashids
    val smsApi = new SMSApi(wSClient)
    var smsID =0
    try {
      smsID = hashids.decode(id).headOption.map(_.toInt).getOrElse(0)
    }catch{
      case ex:ConvertException=> smsID =0
      case _=>smsID =0
    }

    def q(sID: Int) = slickSMS.filter(f=>f.id === sID)
      .join(slickParticipants.filter(_.deletedAt.isEmpty)).on(_.participantID===_.id)
      .join(slickCategories.filter(_.deletedAt.isEmpty)).on(_._1.categoryID === _.id)
      .join(slickTrainings.filter(_.deletedAt.isEmpty)).on(_._1._1.trainingID === _.id)
      .join(slickQuizzes.filter(_.deletedAt.isEmpty)).on(_._1._1._1.quizID === _.id)
    val test = for {
      r<- {
        val query = q(smsID).result
        val statements = query.statements
        db.run(query.headOption)
      }
      questionsOption <- {
        if(r.isDefined){
          db.run(slickQuestions.filter(k => k.deletedAt.isEmpty && k.quizID === r.get._2.id)
                 .join(slickAnswers.filter(_.deletedAt.isEmpty)).on(_.id === _.questionID).result)
        } else {Future(Seq())}
      }
      updateSMS<-updateOpened(smsID)
    }yield {
      r.map { resultSet =>
        val ((((sms, participant), category), training), quiz) = resultSet
          val quests = questionsOption.groupBy(_._1).map(res =>
            TestQuestion(res._1.toQuestion, res._2.map(n => TestAnswer(n._2.id, n._2.text))))
        if (sms.submitted.isEmpty)
          TestModel(participant.toParticipant, quiz.toQuiz, training.toTraining, category.toCategory, quests.toSeq)
        else
          TestModel(participant.toParticipant, quiz.toQuiz, training.toTraining, category.toCategory, Seq())
      }
    }
    test
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
//      .map(_.map(r => {
//      val ((sms, quiz), category) = r
//      (quiz.id, quiz.name, category.id, category.name,sms.status!="pending" )
//    }))

    for{
      res<- db.run(q.result)
    } yield {
      val tes = res.groupBy(r=>(r._1._1.quizID,r._1._2.name,  r._1._1.categoryID,r._2.name,r._1._1.status !="pending")).map(r=>
        UnsentMessages(r._1._1,r._1._2,r._1._3,r._1._4,r._1._5)
      )
      tes.toSeq
      //UnsentMessages (       quizID,quizName,categoryID,categoryName,status)
        //UnsentMessages(r._1,r._2,r._3,r._4,r._5))
    }


  }

  protected def getQueryParticipantLog(id:Int) = {
    slickSMS.filter(_.participantID === id)
      .join(slickQuizzes.filter(_.deletedAt.isEmpty)).on(_.quizID === _.id)
      .join(slickQuestions.filter(_.deletedAt.isEmpty)).on(_._2.id === _.quizID)
      .join(slickAnswers.filter(_.deletedAt.isEmpty)).on(_._2.id === _.questionID)
      .join(slickParticipants.filter(_.deletedAt.isEmpty)).on(_._1._1._1.participantID === _.id)
  }

  override def getParticipantLog(id: Int): Future[Option[ParticipantLog]] = {
    val queryParticipant = slickParticipants.filter(f=>f.id === id && f.deletedAt.isEmpty)
    val q = getQueryParticipantLog(id).filter(_._1._2.correct === true)
      .joinLeft(getQueryParticipantLog(id).join(slickResult)
        .on((j,r)=> j._1._2.id === r.answerID && j._1._1._2.id === r.questionID && j._1._1._1._1.id === r.SMSID))
      .on((j,p)=> //j._1._2.id === p._1._1._2.id && j._2.id === p._1._2.id && //sms
        j._2.id === p._1._2.id && //participant
        j._1._1._2.id === p._1._1._1._2.id && //question
        j._1._1._1._2.id === p._1._1._1._1._2.id) //quiz
    println(q.result.statements)
    for {
      run<-db.run(q.result)
      p<-db.run(queryParticipant.result.headOption)
    }yield{
      if(p.isDefined){
        val test = run.groupBy(_._1._1._1._1._2).map(s=>{ //group by quiz
          val questionLog = s._2.map(res=>{
            val ((((sms,quiz),question),answer),participant) = res._1
            val optionAnswer= if(res._2.isDefined){
              Some(res._2.get._1._1._2.toAnswer)
            }else None
            val answerLog = AnswerLog(answer.toAnswer,optionAnswer)
            QuestionLog(question.toQuestion,answerLog)
          })
          QuizLog(s._1.toQuiz,questionLog)
        })
        Some(ParticipantLog(p.get.toParticipant,test.toSeq))
      }else None
    }

//    AnswerLog(Answer, Option[Answer])
//    QuestionLog(Question, AnswerLog)
//    QuizLog(Quiz, Seq[QuestionLog])
//    ParticipantLog(Participant,Seq[QuizLog])
  }
}


