package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Good, GoodForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[GoodsDAOImpl])
trait GoodsDAO {
  def getAll: Future[Seq[Good]]
  def update(goodID:Int, goodForm: GoodForm): Future[Int]
  def delete(goodID: Int): Future[Int]
  def findByID(goodID: Int): Future[Option[Good]]
  def create(goodForm: GoodForm): Future[Good]
}

class GoodsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends GoodsDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Good]] = {
    val getQuery = slickGoods.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toGood)) } yield all
  }

  override def update(goodID: Int, goodForm: GoodForm): Future[Int] = {
    var updateQuery = slickGoods.filter(f => f.deleted === false && f.id ===goodID)
      .map(u => (u.groupID, u.companyID, u.name, u.description, u.price, u.quantity, u.imageID))
      .update((goodForm.groupID, goodForm.companyID, goodForm.name, goodForm.description, goodForm.price, goodForm.quantity, goodForm.imageID))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(goodID: Int): Future[Int] = {
    val deleteQuery = slickGoods.filter(_.id === goodID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(goodID: Int): Future[Option[Good]] = {
    val findQuery = slickGoods.filter(f => f.deleted === false && f.id === goodID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toGood) else None ) } yield result
  }

  override def create(goodForm: GoodForm): Future[Good] = {
    val dBGood = DBGoods(0, goodForm.groupID, goodForm.companyID, goodForm.name, goodForm.description, goodForm.price, goodForm.quantity, goodForm.imageID, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickGoods.returning(slickGoods) += dBGood
    for { newRow <- db.run(insertQuery).map(r => r.toGood) } yield newRow
  }
}