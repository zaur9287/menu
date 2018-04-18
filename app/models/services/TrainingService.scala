package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.Training
import models.caseClasses.Training._
import models.daos.TrainingsDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[TrainingServiceImpl])
trait TrainingService {
  def get                               :Future[Seq[Training]]
  def create (el: Training)              :Future[Option[Training]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(id:Int)                    :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, updateForm :UpdateFormTraining):Future[Int]
  def findByID(id: Int)                 :Future[Option[Training]]
}

class TrainingServiceImpl @Inject()(DAO: TrainingsDAO) extends TrainingService {
  override def get: Future[Seq[Training]] = {
    for {
      all <- DAO.get
    } yield {
      all
    }
  }
  override def create(el: Training): Future[Option[Training]]                   = DAO.create(el)
  override def delete(id:Int): Future[Int]                                      = DAO.delete(id)
  override def deleteAll: Future[Int]                                           = DAO.deleteAll
  override def update(id: Int, updateForm: UpdateFormTraining): Future[Int]     = DAO.update(id, updateForm)
  override def findByID(id:Int):Future[Option[Training]]                        = DAO.findByID(id)
  override def pureDelete(id:Int):Future[Int]                                   = DAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = DAO.pureDeleteAll
}
