package models.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.{Role, RoleForm}
import models.daos.RoleDAO

import scala.concurrent.Future

@ImplementedBy(classOf[RoleServiceImpl])
trait RoleService {
  def getAll: Future[Seq[Role]]
  def update(roleID: Int, roleForm: RoleForm): Future[Int]
  def delete(roleID: Int): Future[Int]
  def findByID(roleID: Int): Future[Option[Role]]
  def create(roleForm: RoleForm): Future[Role]
}

class RoleServiceImpl @Inject() (roleDAO: RoleDAO) extends RoleService {
  override def getAll: Future[Seq[Role]] = roleDAO.getAll
  override def update(roleID: Int, roleForm: RoleForm): Future[Int] = roleDAO.update(roleID, roleForm)
  override def delete(roleID: Int): Future[Int] = roleDAO.delete(roleID)
  override def findByID(roleID: Int): Future[Option[Role]] = roleDAO.findByID(roleID)
  override def create(roleForm: RoleForm): Future[Role] = roleDAO.create(roleForm)
}

