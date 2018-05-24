package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Company, CompanyForm}
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
}