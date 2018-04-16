package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.SMS
import models.daos.SMSDAO
import models.caseClasses.TestModel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@ImplementedBy(classOf[SMSServiceImpl])
trait SMSService {
  def createRows(cID:Int,qID:Int)       :Future[Int]
  def delete(id:Int)                    :Future[Int]
  def updateOpened(id: Int)             :Future[Int]
  def updateSubmit(id:Int)              :Future[Int]
  def findUnSubmitted(id: Int)          :Future[Boolean]
  def sendSMS                           :Future[Option[Int]]
  def getQuiz(id:String)                :Future[Option[TestModel]]
}

class SMSServiceImpl @Inject()(DAO: SMSDAO) extends SMSService {
  override def createRows(cID:Int,qID:Int): Future[Int]                         = DAO.createRows(cID,qID)
  override def delete(id:Int): Future[Int]                                      = DAO.delete(id)
  override def updateOpened(id: Int): Future[Int]                               = DAO.updateOpened(id)
  override def updateSubmit(id:Int): Future[Int]                                = DAO.updateSubmit(id)
  override def findUnSubmitted(id:Int):Future[Boolean]                          = DAO.findUnSubmitted(id)
  override def sendSMS: Future[Option[Int]]                                     = DAO.sendSMS
  override def getQuiz(id: String): Future[Option[TestModel]]                   = DAO.getQuiz(id)
}
