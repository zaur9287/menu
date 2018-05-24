package models.services

import com.google.inject.Inject
import com.google.inject.ImplementedBy
import models.caseClasses.{Good, GoodForm}
import models.daos.GoodsDAO

import scala.concurrent.Future

@ImplementedBy(classOf[GoodServiceImpl])
trait GoodService {
  def getAll: Future[Seq[Good]]
  def update(goodID:Int, goodForm: GoodForm): Future[Int]
  def delete(goodID: Int): Future[Int]
  def findByID(goodID: Int): Future[Option[Good]]
  def create(goodForm: GoodForm): Future[Good]
}


class GoodServiceImpl @Inject() (goodsDAO: GoodsDAO) extends GoodService {
  override def getAll: Future[Seq[Good]] = goodsDAO.getAll
  override def update(goodID: Int, goodForm: GoodForm): Future[Int] = goodsDAO.update(goodID, goodForm)
  override def delete(goodID: Int): Future[Int] = goodsDAO.delete(goodID)
  override def findByID(goodID: Int): Future[Option[Good]] = goodsDAO.findByID(goodID)
  override def create(goodForm: GoodForm): Future[Good] = goodsDAO.create(goodForm)
}