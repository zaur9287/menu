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
  def getByPage(num:Int)                :Future[(Seq[Participant],Int)]
  def create (el: Participant)          :Future[Option[Participant]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(id:Int)                    :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, u:UpdateFormParticipant):Future[Int]
  def findByID(id: Int)                 :Future[Option[Participant]]
  def findByCategoryID(id: Int)         :Future[Seq[Participant]]
  def fcByPage(id: Int, num: Int)       :Future[(Seq[Participant], Int)]
}

class ParticipantServiceImpl @Inject()(DAO: ParticipantsDAO) extends ParticipantService {
  override def get: Future[Seq[Participant]] = for {all <- DAO.get} yield all
  override def getByPage(num: Int): Future[(Seq[Participant], Int)]             = DAO.getByPage(num)
  override def create(el: Participant): Future[Option[Participant]]             = DAO.create(el)
  override def delete(id:Int): Future[Int]                                      = DAO.delete(id)
  override def deleteAll: Future[Int]                                           = DAO.deleteAll
  override def update(id: Int, u: UpdateFormParticipant): Future[Int]           = DAO.update(id, u)
  override def findByID(id:Int):Future[Option[Participant]]                     = DAO.findByID(id)
  override def fcByPage(id: Int, num: Int):Future[(Seq[Participant], Int)]      = DAO.fcByPage(id,num)
  override def findByCategoryID(id:Int):Future[Seq[Participant]]                = DAO.findByCategoryID(id)
  override def pureDelete(id:Int):Future[Int]                                   = DAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = DAO.pureDeleteAll
}
