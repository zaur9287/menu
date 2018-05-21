package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Image, ImageForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ImageDAOImpl])
trait ImageDAO {
  def getAll: Future[Seq[Image]]
  def update(imageID: Int, imageForm: ImageForm): Future[Int]
  def delete(imageID: Int): Future[Int]
  def findByID(imageID: Int): Future[Option[Image]]
  def create(imageForm: ImageForm): Future[Image]
}

class ImageDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ImageDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Image]] = {
    val getQuery = slickImages.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toImage)) } yield all
  }

  override def update(imageID: Int, imageForm: ImageForm): Future[Int] = {
    val updateQuery = slickImages.filter(f => f.deleted === false && f.id === imageID)
      .map(u => u.path).update(imageForm.path)
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(imageID: Int): Future[Int] = {
    val deleteQuery = slickImages.filter(f => f.deleted === false && f.id === imageID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(imageID: Int): Future[Option[Image]] = {
    val findQuery = slickImages.filter(f => f.deleted === false && f.id === imageID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toImage) else None ) } yield result
  }

  override def create(imageForm: ImageForm): Future[Image] = {
    val dBImage = DBImages(0, imageForm.path, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickImages.returning(slickImages) += dBImage
    for { newRow <- db.run(insertQuery).map(r => r.toImage) } yield newRow
  }
}