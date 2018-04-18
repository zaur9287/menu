package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.Question
import models.caseClasses.Question._
import models.daos.QuestionsDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[QuestionServiceImpl])
trait QuestionService {
  def get                                             :Future[Seq[Question]]
  def create (el: Question)                           :Future[Option[Question]]
  def pureDelete(id:Int)                              :Future[Int]
  def pureDeleteAll                                   :Future[Int]
  def delete(id:Int)                                  :Future[Int]
  def deleteAll                                       :Future[Int]
  def update(id: Int, updateForm:UpdateFormQuestion)  :Future[Int]
  def findByID(id: Int)                               :Future[Option[Question]]
  def findByQuizID(id: Int)                           :Future[Seq[Question]]
}

class QuestionServiceImpl @Inject()(DAO: QuestionsDAO) extends QuestionService {
  override def get: Future[Seq[Question]] = {
    for {
      all <- DAO.get
    } yield {
      all
    }
  }
  override def create(el: Question): Future[Option[Question]]                   = DAO.create(el)
  override def delete(id:Int): Future[Int]                                      = DAO.delete(id)
  override def deleteAll: Future[Int]                                           = DAO.deleteAll
  override def update(id: Int, updateForm: UpdateFormQuestion): Future[Int] = DAO.update(id, updateForm)
  override def pureDelete(id:Int):Future[Int]                                   = DAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = DAO.pureDeleteAll
  override def findByID(id:Int):Future[Option[Question]]                        = DAO.findByID(id)
  override def findByQuizID(id:Int):Future[Seq[Question]]                       = DAO.findByQuizID(id)
}
