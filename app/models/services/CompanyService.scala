package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.Company
import models.caseClasses.CompanyForms.UpdateCompanyForm
import models.daos.CompaniesDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[CompaniesDAOServiceImpl])
trait CompanyService {
  def get                                                   :Future[Seq[Company]]
  def create(Company: Company)                              :Future[Option[Company]]
  def pureDelete(id:Int)                                    :Future[Int]
  def pureDeleteAll                                         :Future[Int]
  def delete(selectedID:Int)                                :Future[Int]
  def deleteAll                                             :Future[Int]
  def update(id: Int, updateCompanyForm: UpdateCompanyForm) :Future[Int]
  def findCompanyByID(id: Int)                              :Future[Option[Company]]
}

class CompaniesDAOServiceImpl @Inject()(CompaniesDAO: CompaniesDAO) extends CompanyService {
  override def get: Future[Seq[Company]] = {
    for {
      allCompanies <- CompaniesDAO.get
    } yield {
      allCompanies
    }
  }
  override def create(Company: Company): Future[Option[Company]]          = CompaniesDAO.create(Company)
  override def delete(selectedID:Int): Future[Int]                        = CompaniesDAO.delete(selectedID)
  override def deleteAll: Future[Int]                                     = CompaniesDAO.deleteAll
  override def update(id: Int, updateCompanyForm: UpdateCompanyForm): Future[Int] = CompaniesDAO.update(id, updateCompanyForm)
  override def findCompanyByID(id:Int):Future[Option[Company]]            = CompaniesDAO.findCompanyByID(id)
  override def pureDelete(id:Int):Future[Int]                             = CompaniesDAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                  = CompaniesDAO.pureDeleteAll
}
