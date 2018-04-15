package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.{ReportRow, Result}
import models.caseClasses.Result._
import models.daos.ResultsDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[ResultServiceImpl])
trait ResultService {
  def create (el: Result)               :Future[Int]
  def createMultiply (rows: Seq[Result]):Future[Int]
  def delete(id:Int)                    :Future[Int]
  def findByID(id: Int)                 :Future[Option[Result]]
  def allReport(tID:Option[Int],cID:Option[Int],qID:Option[Int]):Future[Seq[ReportRow]]
}

class ResultServiceImpl @Inject()(DAO: ResultsDAO) extends ResultService {
  override def create(el: Result): Future[Int]                                = DAO.create(el)
  override def createMultiply (rows: Seq[Result]):Future[Int]                 = DAO.createMultiply(rows)
  override def delete(id:Int): Future[Int]                                    = DAO.delete(id)
  override def findByID(id:Int):Future[Option[Result]]                        = DAO.findByID(id)
  override def allReport(tID:Option[Int],cID:Option[Int],qID:Option[Int]):Future[Seq[ReportRow]] = DAO.allReport(tID,cID,qID)
}
