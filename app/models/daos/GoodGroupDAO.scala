package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{GoodGroup, GoodGroupForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[GoodGroupDAOImpl])
trait GoodGroupDAO {
  def getAll: Future[Seq[GoodGroup]]
  def update(goodGroupID: Int, goodGroupForm: GoodGroupForm): Future[Int]
  def delete(goodGroupID: Int): Future[Int]
  def findByID(goodGroupID: Int): Future[Option[GoodGroup]]
  def create(goodGroupForm: GoodGroupForm): Future[GoodGroup]
}

class GoodGroupDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends GoodGroupDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[GoodGroup]] = {
    val getQuery = slickGoodGroups.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toGoodGroup)) } yield all
  }

  override def update(goodGroupID: Int, goodGroupForm: GoodGroupForm): Future[Int] = {
    val updateQuery = slickGoodGroups.filter(f => f.deleted === false && f.id === goodGroupID)
      .map(u => (u.parentID, u.name, u.imageID))
      .update((goodGroupForm.parentID, goodGroupForm.name, goodGroupForm.imageID))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(goodGroupID: Int): Future[Int] = {
    val deleteQuery = slickGoodGroups.filter(f => f.deleted === false && f.id === goodGroupID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(goodGroupID: Int): Future[Option[GoodGroup]] = {
    val findQuery = slickGoodGroups.filter(f => f.deleted === false && f.id === goodGroupID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toGoodGroup) else None ) } yield result
  }

  override def create(goodGroupForm: GoodGroupForm): Future[GoodGroup] = {
    val dBGoodGroup = DBGoodGroups(0, goodGroupForm.parentID, goodGroupForm.name, goodGroupForm.imageID, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickGoodGroups.returning(slickGoodGroups) += dBGoodGroup
    for { newRow <- db.run(insertQuery).map(r => r.toGoodGroup) } yield newRow
  }
}