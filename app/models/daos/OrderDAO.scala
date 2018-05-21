package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Order, OrderForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OrdersDAOImpl])
trait OrdersDAO {
  def getAll: Future[Seq[Order]]
  def update(orderID:Int, orderForm: OrderForm): Future[Int]
  def delete(orderID: Int): Future[Int]
  def findByID(orderID: Int): Future[Option[Order]]
  def create(orderForm: OrderForm): Future[Order]
}

class OrdersDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends OrdersDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Order]] = {
    val getQuery = slickOrders.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toOrder)) } yield all
  }

  override def update(orderID: Int, orderForm: OrderForm): Future[Int] = {
    var updateQuery = slickOrders.filter(f => f.deleted === false && f.id ===orderID)
      .map(u => (u.tableID, u.companyID, u.userID, u.goodID, u.price, u.quantity, u.status))
      .update((orderForm.tableID, orderForm.companyID, orderForm.userID, orderForm.goodID, orderForm.price, orderForm.quantity, orderForm.status))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(orderID: Int): Future[Int] = {
    val deleteQuery = slickOrders.filter(f => f.deleted === false && f.id === orderID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(orderID: Int): Future[Option[Order]] = {
    val findQuery = slickOrders.filter(f => f.deleted === false && f.id === orderID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toOrder) else None ) } yield result
  }

  override def create(orderForm: OrderForm): Future[Order] = {
    val dBOrder = DBOrder(0, orderForm.tableID, orderForm.companyID, orderForm.userID, orderForm.goodID, orderForm.price, orderForm.quantity, orderForm.status, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickOrders.returning(slickOrders) += dBOrder
    for { newRow <- db.run(insertQuery).map(r => r.toOrder) } yield newRow
  }
}