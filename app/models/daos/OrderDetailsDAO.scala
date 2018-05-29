package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{OrderDetailForm, OrderDetailView}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}
import models.services.OrderService

@ImplementedBy(classOf[OrderDetailsDAOImpl])
trait OrderDetailsDAO {
  def createOrUpdate(orderID: Int, details: Seq[OrderDetailForm]): Future[Seq[OrderDetailView]]
  def findByOrderID(orderID: Int): Future[Seq[OrderDetailView]]
  def deleteByOrderID(orderID: Int): Future[Int]
}

class OrderDetailsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, ordersDAO: OrdersDAO)( implicit executionContext: ExecutionContext) extends OrderDetailsDAO with DBTableDefinitions {

  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def createOrUpdate(orderID: Int, details: Seq[OrderDetailForm]): Future[Seq[OrderDetailView]] = {
    val seqDBOrderDetails = details.map(detail => DBOrderDetails(0, orderID, detail.goodID, detail.price, detail.quantity))
    val insertQuery = slickOrderDetails ++= seqDBOrderDetails
    for {
      deleteOrder <- deleteByOrderID(orderID)
      orderOption <- ordersDAO.findByID(orderID)
      inserts <- if (orderOption.isDefined) { db.run(insertQuery.transactionally) } else Future(Seq())
      result <- findByOrderID(orderID)
    } yield  result
  }

  override def findByOrderID(orderID: Int): Future[Seq[OrderDetailView]] = {
    val findQuery = slickOrderDetails.filter(_.orderID === orderID)
      .join(slickGoods.filter(_.deleted === false)).on(_.goodID === _.id)
    db.run(findQuery.result).map(_.map(result =>
      OrderDetailView(orderID, result._2.toGood, result._1.price, result._1.quantity, result._1.price * result._1.quantity)))
  }

  override def deleteByOrderID(orderID: Int): Future[Int] = {
    val deleteQuery = slickOrderDetails.filter(_.orderID === orderID)
    db.run(deleteQuery.delete).map(result => result)
  }

}