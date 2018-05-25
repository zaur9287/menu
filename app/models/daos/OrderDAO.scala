package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses._
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OrdersDAOImpl])
trait OrdersDAO {
  def getAll: Future[Seq[OrderView]]
  def update(orderID:Int, orderForm: OrderForm): Future[Int]
  def delete(orderID: Int): Future[Int]
  def findByID(orderID: Int): Future[Option[Order]]
  def create(orderForm: OrderForm): Future[Order]
  def getCompanyOrders(companyID: Int): Future[Option[(Company,Seq[Order])]]
}

class OrdersDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends OrdersDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[OrderView]] = {
    val getQuery = slickOrders.filter(_.deleted === false)
      .join(slickTables.filter(_.deleted === false)).on(_.tableID === _.id)
      .join(slickUsers.filter(_.deleted === false)).on(_._1.userID === _.id)
      .join(slickCompanies.filter(_.deleted === false)).on(_._1._1.companyID === _.id)
    for {
      all <- db.run(getQuery.result)
    } yield {
      all.map(a => {
        val (((order, thisTable), thisUser), thisCompany) = a
        OrderView(order.id, thisTable.toTable, thisCompany.toCompany, thisUser.toUser, order.status, order.createdAt, order.updatedAt)
      })
    }
  }

  override def update(orderID: Int, orderForm: OrderForm): Future[Int] = {
    var updateQuery = slickOrders.filter(f => f.deleted === false && f.id ===orderID)
      .map(u => (u.tableID, u.companyID, u.userID, u.status))
      .update((orderForm.tableID, orderForm.companyID, orderForm.userID, orderForm.status))
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
    val dBOrder = DBOrder(0, orderForm.tableID, orderForm.companyID, orderForm.userID, orderForm.status, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickOrders.returning(slickOrders) += dBOrder
    for { newRow <- db.run(insertQuery).map(r => r.toOrder) } yield newRow
  }

  override def getCompanyOrders(companyID: Int): Future[Option[(Company, Seq[Order])]] = {
    val query = slickOrders.filter(f => f.companyID === companyID && f.deleted === false)
      .join(slickCompanies.filter(f => f.id === companyID && f.deleted === false)).sortBy(_._1.id)
    for {
      orders <- db.run(query.result)
    } yield {
      orders.groupBy(_._2).headOption.map(companyOrders =>
        (companyOrders._1.toCompany, companyOrders._2.map(_._1.toOrder)))
    }
  }

}