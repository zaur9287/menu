package models.daos

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.{Image, ImageForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ImageDAOImpl])
trait ImageDAO {
  def getAll: Future[Seq[Image]]
  def create(path: String): Future[Image]
  def update(imageID: Int, path: String): Future[Int]
  def delete(imageID: Int): Future[Int]
  def getImage(imageID: Int): Future[Option[Image]]
}


class ImageDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) (implicit executionContext: ExecutionContext) extends ImageDAO with DBTableDefinitions {

  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Image]] = {
    val query = slickImages.filter(f => f.deleted === false)
    db.run(query.result).map(_.map(result => result.toImage))
  }

  override def create(path: String): Future[Image] = {
    val insertQuery = slickImages.returning(slickImages) += DBImages(0, path, DateTime.now, DateTime.now, false)
    db.run(insertQuery).map(result => result.toImage)
  }

  override def update(imageID: Int, path: String): Future[Int] = {
    val updateQuery = slickImages.filter(f => f.id === imageID && f.deleted === false)
      .map(image => (image.path, image.updatedAt))
      .update((path, DateTime.now))
    db.run(updateQuery).map(result => result)
  }

  override def delete(imageID: Int): Future[Int] = {
    val deleteQuery = slickImages.filter(f => f.id === imageID && f.deleted === false)
    db.run(deleteQuery.delete).map(result => result)
  }

  override def getImage(imageID: Int): Future[Option[Image]] = {
    val getQuery = slickImages.filter(f => f.id === imageID && f.deleted === false)
    db.run(getQuery.result.headOption).map(result => if (result.isDefined) {Some(result.get.toImage)} else None )
  }
}