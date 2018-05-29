package models.services

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{OrderDetailForm, OrderDetailView}
import models.daos.OrderDetailsDAO

import scala.concurrent.Future

@ImplementedBy(classOf[OrderDetailServiceImpl])
trait OrderDetailService {
  def createOrUpdate(orderID: Int, details: Seq[OrderDetailForm]): Future[Seq[OrderDetailView]]
  def findByOrderID(orderID: Int): Future[Seq[OrderDetailView]]
  def deleteByOrderID(orderID: Int): Future[Int]
}

class OrderDetailServiceImpl @Inject() (orderDetailsDAO: OrderDetailsDAO) extends OrderDetailService {
  override def createOrUpdate(orderID: Int, details: Seq[OrderDetailForm]): Future[Seq[OrderDetailView]] = orderDetailsDAO.createOrUpdate(orderID, details)
  override def findByOrderID(orderID: Int): Future[Seq[OrderDetailView]] = orderDetailsDAO.findByOrderID(orderID)
  override def deleteByOrderID(orderID: Int): Future[Int] = orderDetailsDAO.deleteByOrderID(orderID)
}