package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses._
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[JobsDAOImpl])
trait JobsDAO {
  def getAll(filters: JobFilterForm): Future[Seq[JobView]]
  def update(jobID:Int, jobForm: JobForm): Future[Int]
  def delete(jobID: Int): Future[Int]
  def findByID(jobID: Int): Future[Option[Job]]
  def create(jobForm: JobForm): Future[Job]
}

class JobsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends JobsDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll(filters: JobFilterForm): Future[Seq[JobView]] = {
    var query = slickJobs.filter(_.deleted === false)
      .join(slickUsers.filter(_.deleted === false)).on(_.userID === _.id)
      .join(slickCompanies.filter(_.deleted === false)).on(_._1.companyID === _.id)
      .join(slickRoles.filter(_.deleted === false)).on(_._1._1.roleID === _.id)
      .sortBy(_._1._1._1.id)

    filters.userID.foreach(f => query = query.filter(_._1._1._2.id === f))
    filters.companyID.foreach(f => query = query.filter(_._1._2.id === f))
    filters.roleID.foreach(f => query = query.filter(_._2.id === f))

    for {
      jobViews <- db.run(query.result).map(r => r)
    } yield jobViews.map(jobView => {
      val (((job, user), company), role) = jobView
      JobView(job.id, user.toUser, company.toCompany, role.toRole, job.name, job.description, job.createdAt, job.updatedAt)
    })
  }

  override def update(jobID: Int, jobForm: JobForm): Future[Int] = {
    var updateQuery = slickJobs.filter(f => f.deleted === false && f.id ===jobID)
      .map(u => (u.userID, u.companyID, u.roleID, u.name, u.description))
      .update((jobForm.userID, jobForm.companyID, jobForm.roleID, jobForm.name, jobForm.description))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(jobID: Int): Future[Int] = {
    val deleteQuery = slickJobs.filter(f => f.deleted === false && f  .id === jobID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(jobID: Int): Future[Option[Job]] = {
    val findQuery = slickJobs.filter(f => f.deleted === false && f.id === jobID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toJob) else None ) } yield result
  }

  override def create(jobForm: JobForm): Future[Job] = {
    val dBJob = DBJobs(0, jobForm.userID, jobForm.companyID, jobForm.roleID, jobForm.name, jobForm.description, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickJobs.returning(slickJobs) += dBJob
    for { newRow <- db.run(insertQuery).map(r => r.toJob) } yield newRow
  }
}