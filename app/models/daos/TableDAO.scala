package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Table_, TableForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TableDAOImpl])
trait TableDAO {
  def getAll: Future[Seq[Table_]]
  def update(tableID: Int, tableForm: TableForm): Future[Int]
  def delete(tableID: Int): Future[Int]
  def findByID(tableID: Int): Future[Option[Table_]]
  def create(tableForm: TableForm): Future[Table_]
}

class TableDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends TableDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Table_]] = {
    val getQuery = slickTables.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toTable)) } yield all
  }

  override def update(tableID: Int, tableForm: TableForm): Future[Int] = {
    val updateQuery = slickTables.filter(f => f.deleted === false && f.id === tableID)
      .map(u => (u.companyID, u.name, u.description, u.imageID))
      .update((tableForm.companyID, tableForm.name, tableForm.description, tableForm.imageID))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(tableID: Int): Future[Int] = {
    val deleteQuery = slickTables.filter(f => f.deleted === false && f.id === tableID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(tableID: Int): Future[Option[Table_]] = {
    val findQuery = slickTables.filter(f => f.deleted === false && f.id === tableID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toTable) else None ) } yield result
  }

  override def create(tableForm: TableForm): Future[Table_] = {
    val dBTable = DBTables(0, tableForm.companyID, tableForm.name, tableForm.description, tableForm.imageID, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickTables.returning(slickTables) += dBTable
    for { newRow <- db.run(insertQuery).map(r => r.toTable) } yield newRow
  }
}