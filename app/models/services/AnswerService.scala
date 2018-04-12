package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.Answer
import models.caseClasses.Answer._
import models.daos.AnswersDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[AnswerServiceImpl])
trait AnswerService {
  def get                               :Future[Seq[Answer]]
  def create (el: Answer)               :Future[Option[Answer]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(id:Int)                    :Future[Int]
  def deleteByQuestionID(id:Int)        :Future[Int]
  def pureDeleteByQuestionID(id:Int)    :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, updateForm        :UpdateFormAnswer):Future[Option[Answer]]
  def findByID(id: Int)                 :Future[Option[Answer]]
  def findByQuestionID(id: Int)         :Future[Seq[Answer]]
}

class AnswerServiceImpl @Inject()(DAO: AnswersDAO) extends AnswerService {
  override def get: Future[Seq[Answer]] = {
    for {
      all <- DAO.get
    } yield {
      all
    }
  }
  override def create(el: Answer): Future[Option[Answer]]                       = DAO.create(el)
  override def delete(id:Int): Future[Int]                                      = DAO.delete(id)
  override def deleteByQuestionID(id:Int): Future[Int]                          = DAO.deleteByQuestionID(id)
  override def deleteAll: Future[Int]                                           = DAO.deleteAll
  override def update(id: Int, updateForm: UpdateFormAnswer): Future[Option[Answer]] = DAO.update(id, updateForm)
  override def pureDelete(id:Int):Future[Int]                                   = DAO.pureDelete(id)
  override def pureDeleteByQuestionID(id:Int):Future[Int]                       = DAO.pureDeleteByQuestionID(id)
  override def pureDeleteAll:Future[Int]                                        = DAO.pureDeleteAll
  override def findByID(id:Int):Future[Option[Answer]]                          = DAO.findByID(id)
  override def findByQuestionID(id:Int):Future[Seq[Answer]]                     = DAO.findByQuestionID(id)
}
