package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Company, CompanyForm, Contact, User}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CompanyDAOImpl])
trait CompanyDAO {
  def getAll: Future[Seq[Company]]
  def update(companyID: Int, companyForm: CompanyForm): Future[Int]
  def delete(companyID: Int): Future[Int]
  def findByID(companyID: Int): Future[Option[Company]]
  def create(companyForm: CompanyForm): Future[Company]
  def createWith(company: Company): Future[Company]
  def getCompanyUsers(companyID: Int): Future[Seq[User]]
  def getCompanyContacts(companyID: Int): Future[Seq[Contact]]
}

class CompanyDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends CompanyDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Company]] = {
    val getQuery = slickCompanies.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toCompany)) } yield all
  }

  override def update(companyID: Int, companyForm: CompanyForm): Future[Int] = {
    val updateQuery = slickCompanies.filter(f => f.deleted === false && f.id === companyID)
      .map(u => (u.name, u.description, u.imageID, u.updatedAt))
      .update((companyForm.name, companyForm.description, companyForm.imageID, DateTime.now))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(companyID: Int): Future[Int] = {
    val deleteQuery = slickCompanies.filter(f => f.deleted === false && f.id === companyID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))

    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(companyID: Int): Future[Option[Company]] = {
    val findQuery = slickCompanies.filter(f => f.deleted === false && f.id === companyID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toCompany) else None ) } yield result
  }

  override def create(companyForm: CompanyForm): Future[Company] = {
    val dBCompany = DBCompanies(0, companyForm.name, companyForm.description, companyForm.imageID, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickCompanies.returning(slickCompanies) += dBCompany
    for { newRow <- db.run(insertQuery).map(r => r.toCompany) } yield newRow
  }

  override def createWith(company: Company): Future[Company] = {
    val dBCompany = DBCompanies(0, company.name, company.description, company.imageID, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickCompanies.returning(slickCompanies) += dBCompany
    for { newRow <- db.run(insertQuery).map(r => r.toCompany) } yield newRow
  }

  override def getCompanyUsers(companyID: Int): Future[Seq[User]] = {
    val query = slickCompanies.filter(f => f.id === companyID && f.deleted === false)
        .join(slickJobs.filter(f => f.companyID === companyID && f.deleted === false)).on(_.id === _.companyID)
      .join(slickUsers.filter(_.deleted === false)).on(_._2.userID === _.id).sortBy(_._2.createdAt)
    for {
      users <- db.run(query.result).map(r => r)
    } yield users.map(_._2.toUser)
  }

  override def getCompanyContacts(companyID: Int): Future[Seq[Contact]] = {
    val query = slickCompanies.filter(f => f.id === companyID && f.deleted === false)
        .join(slickContacts.filter(_.deleted === false)).on(_.id === _.companyID).sortBy(_._2.id)
    db.run(query.result).map(_.map(_._2.toContact))
  }

}