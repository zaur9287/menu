package  models.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.{Job, JobForm}
import models.daos.JobsDAO

import scala.concurrent.Future

@ImplementedBy(classOf[JobServiceImpl])
trait JobService {
  def getAll: Future[Seq[Job]]
  def update(jobID: Int, jobForm: JobForm): Future[Int]
  def create(jobForm: JobForm): Future[Job]
  def delete(jobID: Int): Future[Int]
  def findByID(jobID: Int): Future[Option[Job]]
}

class JobServiceImpl @Inject() (jobsDAO: JobsDAO) extends JobService {
  override def getAll: Future[Seq[Job]] = jobsDAO.getAll
  override def update(jobID: Int, jobForm: JobForm): Future[Int] = jobsDAO.update(jobID, jobForm)
  override def create(jobForm: JobForm): Future[Job] = jobsDAO.create(jobForm)
  override def delete(jobID: Int): Future[Int] = jobsDAO.delete(jobID)
  override def findByID(jobID: Int): Future[Option[Job]] = jobsDAO.findByID(jobID)
}