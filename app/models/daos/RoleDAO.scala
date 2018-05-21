package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Role, RoleForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[RoleDAOImpl])
trait RoleDAO {
  def getAll: Future[Seq[Role]]
  def update(roleID: Int, roleForm: RoleForm): Future[Int]
  def delete(roleID: Int): Future[Int]
  def findByID(roleID: Int): Future[Option[Role]]
  def create(roleForm: RoleForm): Future[Role]
}

class RoleDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends RoleDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Role]] = {
    val getQuery = slickRoles.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toRole)) } yield all
  }

  override def update(roleID: Int, roleForm: RoleForm): Future[Int] = {
    val updateQuery = slickRoles.filter(f => f.deleted === false && f.id === roleID)
      .map(_.name).update(roleForm.name)
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(roleID: Int): Future[Int] = {
    val deleteQuery = slickRoles.filter(_.id === roleID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(roleID: Int): Future[Option[Role]] = {
    val findQuery = slickRoles.filter(f => f.deleted === false && f.id === roleID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toRole) else None ) } yield result
  }

  override def create(roleForm: RoleForm): Future[Role] = {
    val dBRole = DBRoles(0, roleForm.name, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickRoles.returning(slickRoles) += dBRole
    for { newRow <- db.run(insertQuery).map(r => r.toRole) } yield newRow
  }
}