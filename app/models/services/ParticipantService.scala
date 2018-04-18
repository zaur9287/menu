package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.Participant
import models.caseClasses.Participant._
import models.daos.ParticipantsDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[ParticipantServiceImpl])
trait ParticipantService {
  def get                               :Future[Seq[Participant]]
  def create (el: Participant)          :Future[Option[Participant]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(id:Int)                    :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, updateForm:UpdateFormParticipant):Future[Int]
  def findByID(id: Int)                 :Future[Option[Participant]]
  def findByCategoryID(id: Int)         :Future[Seq[Participant]]
}

class ParticipantServiceImpl @Inject()(DAO: ParticipantsDAO) extends ParticipantService {
  override def get: Future[Seq[Participant]] = {
    for {
      all <- DAO.get
    } yield {
      all
    }
  }
  override def create(el: Participant): Future[Option[Participant]]             = DAO.create(el)
  override def delete(id:Int): Future[Int]                                      = DAO.delete(id)
  override def deleteAll: Future[Int]                                           = DAO.deleteAll
  override def update(id: Int, updateForm: UpdateFormParticipant): Future[Int]  = DAO.update(id, updateForm)
  override def findByID(id:Int):Future[Option[Participant]]                     = DAO.findByID(id)
  override def findByCategoryID(id:Int):Future[Seq[Participant]]                = DAO.findByCategoryID(id)
  override def pureDelete(id:Int):Future[Int]                                   = DAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = DAO.pureDeleteAll
}
