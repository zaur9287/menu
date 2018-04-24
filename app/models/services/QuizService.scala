package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.{Answer, Question, Quiz}
import models.caseClasses.Quiz._
import models.daos.QuizzesDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[QuizServiceImpl])
trait QuizService {
  def get                               :Future[Seq[Quiz]]
  def getByPage(num:Int)                :Future[(Seq[Quiz],Int)]
  def getQuestions(id:Int)              :Future[Option[(Question,Seq[Answer])]]
  def create (el: Quiz)                 :Future[Option[Quiz]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(id:Int)                    :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, updateForm        :UpdateFormQuiz):Future[Int]
  def findBySearchForm(tID:Int,cID:Int) :Future[Seq[Quiz]]
  def findByID(id: Int)                 :Future[Option[Quiz]]
  def findByCategoryID(id: Int)         :Future[Seq[Quiz]]
  def findByTrainingID(id: Int)         :Future[Seq[Quiz]]
}

class QuizServiceImpl @Inject()(DAO: QuizzesDAO) extends QuizService {
  override def get: Future[Seq[Quiz]] = for ( all <- DAO.get  ) yield all
  override def getByPage(num: Int): Future[(Seq[Quiz], Int)]                    = DAO.getByPage(num)
  override def getQuestions(id:Int): Future[Option[(Question,Seq[Answer])]]     = DAO.getQuestions(id)
  override def create(el: Quiz): Future[Option[Quiz]]                           = DAO.create(el)
  override def delete(id:Int): Future[Int]                                      = DAO.delete(id)
  override def deleteAll: Future[Int]                                           = DAO.deleteAll
  override def update(id: Int, updateForm: UpdateFormQuiz): Future[Int]         = DAO.update(id, updateForm)
  override def pureDelete(id:Int):Future[Int]                                   = DAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = DAO.pureDeleteAll
  override def findByID(id:Int):Future[Option[Quiz]]                            = DAO.findByID(id)
  override def findByCategoryID(id:Int):Future[Seq[Quiz]]                       = DAO.findByCategoryID(id)
  override def findByTrainingID(id:Int):Future[Seq[Quiz]]                       = DAO.findByTrainingID(id)
  override def findBySearchForm(tID:Int,cID:Int): Future[Seq[Quiz]]             = DAO.findBySearchForm(tID,cID)

}
