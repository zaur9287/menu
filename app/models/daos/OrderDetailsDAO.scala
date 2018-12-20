package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Order, OrderDetailForm, OrderDetailView}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}
import models.services.OrderService

@ImplementedBy(classOf[OrderDetailsDAOImpl])
trait OrderDetailsDAO {
  def createOrUpdate(order: Order, details: Seq[OrderDetailForm]): Future[Seq[OrderDetailView]]
  def findByOrderID(orderID: Int): Future[Seq[OrderDetailView]]
  def deleteByOrderID(orderID: Int): Future[Int]
}

class OrderDetailsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)( implicit executionContext: ExecutionContext) extends OrderDetailsDAO with DBTableDefinitions {

  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def createOrUpdate(order: Order, details: Seq[OrderDetailForm]): Future[Seq[OrderDetailView]] = {
    val seqDBOrderDetails = details.map(detail => DBOrderDetails(0, order.id, detail.goodID, detail.price, detail.quantity))
    val insertQuery = slickOrderDetails ++= seqDBOrderDetails
    for {
      deleteOrder <- deleteByOrderID(order.id)
      inserts <- db.run(insertQuery.transactionally)
      result <- findByOrderID(order.id)
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