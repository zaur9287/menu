package models.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.{TableForm, Table_}
import models.daos.TableDAO

import scala.concurrent.Future


@ImplementedBy(classOf[TableServiceImpl])
trait TableService {
  def getAll: Future[Seq[Table_]]
  def create(tableForm: TableForm): Future[Table_]
  def update(tableID: Int, tableForm: TableForm): Future[Int]
  def findByID(tableID: Int): Future[Option[Table_]]
  def delete(tableID: Int): Future[Int]
}

class TableServiceImpl @Inject()(tableDAO: TableDAO) extends TableService {
  override def getAll: Future[Seq[Table_]] = tableDAO.getAll
  override def create(tableForm: TableForm): Future[Table_] = tableDAO.create(tableForm)
  override def update(tableID: Int, tableForm: TableForm): Future[Int] = tableDAO.update(tableID, tableForm)
  override def findByID(tableID: Int): Future[Option[Table_]] = tableDAO.findByID(tableID)
  override def delete(tableID: Int): Future[Int] = tableDAO.delete(tableID)
}