package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses._
import models.services.OrderDetailService
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OrdersDAOImpl])
trait OrdersDAO {
  def getAll(filters: OrderFilterForm): Future[Seq[OrderView]]
  def update(orderID:Int, orderForm: OrderForm): Future[Option[OrderView]]
  def delete(orderID: Int): Future[Int]
  def findByID(orderID: Int): Future[Option[OrderView]]
  def create(orderForm: OrderForm): Future[OrderView]
  def getCompanyOrders(companyID: Int): Future[Option[(Company,Seq[Order])]]
}

class OrdersDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, orderDetailsDAO: OrderDetailsDAO)( implicit executionContext: ExecutionContext) extends OrdersDAO with DBTableDefinitions{

  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll(filters: OrderFilterForm): Future[Seq[OrderView]] = {
    var getQuery = slickOrders.filter(_.deleted === false)
      .join(slickTables.filter(_.deleted === false)).on(_.tableID === _.id)
      .join(slickUsers.filter(_.deleted === false)).on(_._1.userID === _.id)
      .join(slickCompanies.filter(_.deleted === false)).on(_._1._1.companyID === _.id)
      .join(slickOrderDetails).on(_._1._1._1.id === _.orderID)
      .join(slickGoods.filter(_.deleted === false)).on(_._2.goodID === _.id).sortBy(_._1._1._1._1._1.id)

    filters.tableID.foreach(f => getQuery =  getQuery.filter(_._1._1._1._1._2.id === f))
    filters.companyID.foreach(f => getQuery =  getQuery.filter(_._1._1._2.id === f))
    filters.userID.foreach(f => getQuery =  getQuery.filter(_._1._1._1._2.id === f))
    filters.status.foreach(f => getQuery =  getQuery.filter(_._1._1._1._1._1.status === f))
    filters.goodID.foreach(f => getQuery =  getQuery.filter(_._2.id === f))

    for {
      all <- db.run(getQuery.result)
    } yield {
      all.groupBy(g => {
        val (((((order, table), user), company), orderDetails), good) = g
        (order, table, user, company)
      }).map(m => {
        val (order, table, user, company) = m._1
        val details = m._2.map(d => {
          val (((((order, table), user), company), orderDetails), good) = d
          OrderDetailView(order.id, good.toGood, good.price, good.quantity, good.price * good.quantity)
        })
        OrderView(order.id, table.toTable, company.toCompany, user.toUser, order.status, order.createdAt, order.updatedAt, details)
      }).toSeq
    }
  }

  override def update(orderID: Int, orderForm: OrderForm): Future[Option[OrderView]] = {
    var updateQuery = slickOrders.filter(f => f.deleted === false && f.id ===orderID)
      .map(u => (u.tableID, u.companyID, u.userID, u.status))
      .update((orderForm.tableID, orderForm.companyID, orderForm.userID, orderForm.status))
    for {
      updated <- db.run(updateQuery.transactionally).map(r => r)
      result <- findByID(orderID)
    } yield result
  }

  override def delete(orderID: Int): Future[Int] = {
    val deleteQuery = slickOrders.filter(f => f.deleted === false && f.id === orderID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(orderID: Int): Future[Option[OrderView]] = {
    val findQuery = slickOrders.filter(f => f.deleted === false && f.id === orderID)
      .join(slickTables.filter(_.deleted === false)).on(_.tableID === _.id)
      .join(slickUsers.filter(_.deleted === false)).on(_._1.userID === _.id)
      .join(slickCompanies.filter(_.deleted === false)).on(_._1._1.companyID === _.id)
      .join(slickOrderDetails).on(_._1._1._1.id === _.orderID)
      .join(slickGoods.filter(_.deleted === false)).on(_._2.goodID === _.id).sortBy(_._1._2.id)
    for {
      all <- db.run(findQuery.result)
    } yield {
      all.groupBy(g => {
        val (((((order, table), user), company), orderDetails), good) = g
        (order, table, user, company)
      }).headOption.map(m => {
        val (order, table, user, company) = m._1
        val details = m._2.map(d => {
          val (((((order, table), user), company), orderDetails), good) = d
          OrderDetailView(order.id, good.toGood, good.price, good.quantity, good.price * good.quantity)
        })
        OrderView(order.id, table.toTable, company.toCompany, user.toUser, order.status, order.createdAt, order.updatedAt, details)
      })
    }
  }

  override def create(orderForm: OrderForm): Future[OrderView] = {
    val dBOrder = DBOrder(0, orderForm.tableID, orderForm.companyID, orderForm.userID, orderForm.status, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickOrders.returning(slickOrders) += dBOrder

    for {
      order <- db.run(insertQuery).map(r => r.toOrder)
      details <- orderDetailsDAO.createOrUpdate(order.id, orderForm.details)
      orderView <- findByID(order.id)
    } yield orderView.get
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