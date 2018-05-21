package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Job, JobForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[JobsDAOImpl])
trait JobsDAO {
  def getAll: Future[Seq[Job]]
  def update(jobID:Int, jobForm: JobForm): Future[Int]
  def delete(jobID: Int): Future[Int]
  def findByID(jobID: Int): Future[Option[Job]]
  def create(jobForm: JobForm): Future[Job]
}

class JobsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends JobsDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Job]] = {
    val getQuery = slickJobs.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toJob)) } yield all
  }

  override def update(jobID: Int, jobForm: JobForm): Future[Int] = {
    var updateQuery = slickJobs.filter(f => f.deleted === false && f.id ===jobID)
      .map(u => (u.userID, u.companyID, u.roleID, u.name, u.description))
      .update((jobForm.userID, jobForm.companyID, jobForm.roleID, jobForm.name, jobForm.description))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(jobID: Int): Future[Int] = {
    val deleteQuery = slickJobs.filter(f => f.deleted === false && f.id === jobID)
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