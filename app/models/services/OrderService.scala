package models.services


import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses._
import models.daos.OrdersDAO

import scala.concurrent.Future

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getAll(filters: OrderFilterForm): Future[Seq[OrderView]]
  def update(orderID:Int, orderForm: OrderForm): Future[Option[OrderView]]
  def delete(orderID: Int): Future[Int]
  def findByID(orderID: Int): Future[Option[OrderView]]
  def create(orderForm: OrderForm): Future[OrderView]
  def getCompanyOrders(companyID: Int): Future[Option[(Company, Seq[Order])]]
}


class OrderServiceImpl @Inject()(ordersDAO: OrdersDAO) extends OrderService {
  override def getAll(filters: OrderFilterForm): Future[Seq[OrderView]] = ordersDAO.getAll(filters)
  override def update(orderID: Int, orderForm: OrderForm): Future[Option[OrderView]] = ordersDAO.update(orderID, orderForm)
  override def delete(orderID: Int): Future[Int] = ordersDAO.delete(orderID)
  override def findByID(orderID: Int): Future[Option[OrderView]] = ordersDAO.findByID(orderID)
  override def create(orderForm: OrderForm): Future[OrderView] = ordersDAO.create(orderForm)
  override def getCompanyOrders(companyID: Int): Future[Option[(Company, Seq[Order])]] = ordersDAO.getCompanyOrders(companyID)
}