package models.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.{Company, Order, OrderForm, OrderView}
import models.daos.OrdersDAO

import scala.concurrent.Future

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getAll: Future[Seq[OrderView]]
  def update(orderID:Int, orderForm: OrderForm): Future[Int]
  def delete(orderID: Int): Future[Int]
  def findByID(orderID: Int): Future[Option[Order]]
  def create(orderForm: OrderForm): Future[Order]
  def getCompanyOrders(companyID: Int): Future[Option[(Company, Seq[Order])]]
}


class OrderServiceImpl @Inject()(ordersDAO: OrdersDAO) extends OrderService {
  override def getAll: Future[Seq[OrderView]] = ordersDAO.getAll
  override def update(orderID: Int, orderForm: OrderForm): Future[Int] = ordersDAO.update(orderID, orderForm)
  override def delete(orderID: Int): Future[Int] = ordersDAO.delete(orderID)
  override def findByID(orderID: Int): Future[Option[Order]] = ordersDAO.findByID(orderID)
  override def create(orderForm: OrderForm): Future[Order] = ordersDAO.create(orderForm)

  override def getCompanyOrders(companyID: Int): Future[Option[(Company, Seq[Order])]] = ordersDAO.getCompanyOrders(companyID)
}