package models.services

import com.google.inject.Inject
import com.google.inject.ImplementedBy
import models.caseClasses.{GoodGroup, GoodGroupForm}
import models.daos.GoodGroupDAO

import scala.concurrent.Future

@ImplementedBy(classOf[GoodGroupServiceImpl])
trait GoodGroupService {
  def getAll: Future[Seq[GoodGroup]]
  def update(goodGroupID: Int, goodGroupForm: GoodGroupForm): Future[Int]
  def delete(goodGroupID: Int): Future[Int]
  def findByID(goodGroupID: Int): Future[Option[GoodGroup]]
  def create(goodGroupForm: GoodGroupForm): Future[GoodGroup]
}


class GoodGroupServiceImpl @Inject() (goodGroupDAO: GoodGroupDAO) extends GoodGroupService {
  override def getAll: Future[Seq[GoodGroup]] = goodGroupDAO.getAll
  override def update(goodGroupID: Int, goodGroupForm: GoodGroupForm): Future[Int] = goodGroupDAO.update(goodGroupID, goodGroupForm)
  override def delete(goodGroupID: Int): Future[Int] = goodGroupDAO.delete(goodGroupID)
  override def findByID(goodGroupID: Int): Future[Option[GoodGroup]] = goodGroupDAO.findByID(goodGroupID)
  override def create(goodGroupForm: GoodGroupForm): Future[GoodGroup] = goodGroupDAO.create(goodGroupForm)
}