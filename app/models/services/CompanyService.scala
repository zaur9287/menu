package models.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.{Company, CompanyForm, Contact, User}
import models.daos.CompanyDAO

import scala.concurrent.Future


@ImplementedBy(classOf[CompanyServiceImpl])
trait CompanyService {
  def getAll: Future[Seq[Company]]
  def update(companyID: Int, companyForm: CompanyForm): Future[Int]
  def delete(companyID: Int): Future[Int]
  def findByID(companyID: Int): Future[Option[Company]]
  def create(companyForm: CompanyForm): Future[Company]
  def getCompanyUsers(companyID: Int): Future[Seq[User]]
  def getCompanyContacts(companyID: Int): Future[Seq[Contact]]
}

class CompanyServiceImpl @Inject() (serviceDAO: CompanyDAO) extends CompanyService{
  override def getAll: Future[Seq[Company]] = serviceDAO.getAll
  override def update(companyID: Int, companyForm: CompanyForm): Future[Int] = serviceDAO.update(companyID, companyForm)
  override def delete(companyID: Int): Future[Int] = serviceDAO.delete(companyID)
  override def findByID(companyID: Int): Future[Option[Company]] = serviceDAO.findByID(companyID)
  override def create(companyForm: CompanyForm): Future[Company] = serviceDAO.create(companyForm)
  override def getCompanyUsers(companyID: Int): Future[Seq[User]] = serviceDAO.getCompanyUsers(companyID)

  override def getCompanyContacts(companyID: Int): Future[Seq[Contact]] = serviceDAO.getCompanyContacts(companyID)
}