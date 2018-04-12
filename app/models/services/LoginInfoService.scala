package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.LInfo
import models.daos.LoginInfosDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[LoginInfoServiceImpl])
trait LoginInfoService {
  def create (currRow: LInfo)       :Future[Option[LInfo]]
  def delete(selectedID:Int)        :Future[Int]
  def deleteAll                     :Future[Int]
  def findByUserID(id: String)      :Future[Option[LInfo]]
}

class LoginInfoServiceImpl @Inject()(loginInfoDAO: LoginInfosDAO) extends LoginInfoService {
  override def create(currRow: LInfo): Future[Option[LInfo]]            = loginInfoDAO.create(currRow)
  override def delete(selectedID:Int): Future[Int]                      = loginInfoDAO.delete(selectedID)
  override def deleteAll: Future[Int]                                   = loginInfoDAO.deleteAll
  override def findByUserID(id:String):Future[Option[LInfo]]            = loginInfoDAO.findByUserID(id)
}
