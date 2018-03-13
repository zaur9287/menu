package models.daos

import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Company
import models.caseClasses.CompanyForms.UpdateCompanyForm
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CompaniesDAOImpl])
trait CompaniesDAO {
  def get: Future[Seq[Company]]
  def create(Company: Company): Future[Option[Company]]
  def pureDelete(id:Int): Future[Int]
  def pureDeleteAll: Future[Int]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(id:Int,updateCompanyForm: UpdateCompanyForm): Future[Option[Company]]
  def findCompanyByID(Companyid: Int): Future[Option[Company]]
}

class CompaniesDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends CompaniesDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Company]] = {
    val query = slickCompanies.filter(r => r.deleted_at.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toCompanies).sortBy(_.id)
    )
  }
//bir sətrin yaradılmasıı və yaranmış sətrin geri qaytarılması
  override def create(Company: Company): Future[Option[Company]] = {
    val dBCompany = DBCompany(Company.id, Company.name, Company.description, Company.createdAt, Company.updatedAt, None)
    val query = slickCompanies.returning(slickCompanies) += dBCompany
    db.run(query).map ( r => Some(r.toCompanies) )
  }
//id-yə görə birinin silinməsi (seçilən sətr bazan silinir.)
  override def delete(selectedID:Int): Future[Int] = {
    val selectedCompany= slickCompanies.filter(_.id===selectedID)
    val deleteAction = selectedCompany.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }
//bütün sətirlər bazadan silinir.
  override def deleteAll: Future[Int] = {
    val selectAllCompanies = slickCompanies
    val deletingAllAction = selectAllCompanies.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }
//göndərilmiş məlumatların update olunması. və update olunmuş sətrin geri qaytarılması
  override  def update(id:Int,updateCompanyForm: UpdateCompanyForm): Future[Option[Company]] = {
    val updateQuery = slickCompanies.filter(c => c.id === id && c.deleted_at.isEmpty)
      .map(c => (c.name, c.desc, c.updated_at))
      .update((updateCompanyForm.name, Some(updateCompanyForm.description), DateTime.now))

    val result = for {
      getUpdatedCompany <- findCompanyByID(id)
      updateRowCount <- if(getUpdatedCompany.isDefined){ db.run(updateQuery) } else { Future(0) }
    } yield {
      if(updateRowCount > 0){
        getUpdatedCompany.map(c => c.copy(name = updateCompanyForm.name, description = Some(updateCompanyForm.description), updatedAt = c.updatedAt))
      } else {None}
    }
    result
 }
//id-yə görə birinin tapılması
  override def findCompanyByID(Companyid:Int): Future[Option[Company]] = {
    val query = slickCompanies.filter(f=>f.id === Companyid && f.deleted_at.isEmpty).result
    db.run(query.headOption).map(_.map(_.toCompanies))
  }
//id-yə görə birinin silinməsi (bazada silinmə tarixinin update olunması)
  override def pureDelete(id:Int): Future[Int] = {
    val query = slickCompanies.filter(f=>f.id === id && f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
//bütün sətrlərdə silinmə tarixini update olunması
  override def pureDeleteAll: Future[Int] = {
    val query = slickCompanies.filter(f=>f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


