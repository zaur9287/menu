package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.{Category, Participant}
import models.caseClasses.Category._
import models.daos.CategoriesDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[CategoryServiceImpl])
trait CategoryService {
  def get                               :Future[Seq[Category]]
  def getByPage(num:Int)                :Future[(Seq[Category],Int)]
  def create (el: Category)             :Future[Option[Category]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(id:Int)                    :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, updateForm        :UpdateFormCategory):Future[Int]
  def findByID(id: Int)                 :Future[Option[Category]]
}

class CategoryServiceImpl @Inject()(DAO: CategoriesDAO) extends CategoryService {
  override def get: Future[Seq[Category]] = {for (all <- DAO.get) yield all}
  override def getByPage(num: Int): Future[(Seq[Category], Int)]                = DAO.getByPage(num)
  override def create(el: Category): Future[Option[Category]]                   = DAO.create(el)
  override def delete(id:Int): Future[Int]                                      = DAO.delete(id)
  override def deleteAll: Future[Int]                                           = DAO.deleteAll
  override def update(id: Int, updateForm: UpdateFormCategory): Future[Int]     = DAO.update(id, updateForm)
  override def findByID(id:Int):Future[Option[Category]]                        = DAO.findByID(id)
  override def pureDelete(id:Int):Future[Int]                                   = DAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = DAO.pureDeleteAll
}
