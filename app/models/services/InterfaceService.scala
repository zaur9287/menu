package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.Interface
import models.caseClasses.InterfaceForms.UpdateInterfaceForm
import models.daos.InterfacesDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[InterfaceServiceImpl])
trait InterfaceService {
  def get                               :Future[Seq[Interface]]
  def create (currRow: Interface)       :Future[Option[Interface]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(selectedID:Int)            :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, updateInterfaceForm  :UpdateInterfaceForm):Future[Option[Interface]]
  def findInterfaceByID(id: Int)     :Future[Option[Interface]]
}

class InterfaceServiceImpl @Inject()(interfaceDAO: InterfacesDAO) extends InterfaceService {
  override def get: Future[Seq[Interface]] = {
    for {
      allInterfaces <- interfaceDAO.get
    } yield {
      allInterfaces
    }
  }
  override def create(currRow: Interface): Future[Option[Interface]]            = interfaceDAO.create(currRow)
  override def delete(selectedID:Int): Future[Int]                              = interfaceDAO.delete(selectedID)
  override def deleteAll: Future[Int]                                           = interfaceDAO.deleteAll
  override def update(id: Int, updateInterfaceForm: UpdateInterfaceForm): Future[Option[Interface]] = interfaceDAO.update(id, updateInterfaceForm)
  override def findInterfaceByID(id:Int):Future[Option[Interface]]              = interfaceDAO.findInterfaceByID(id)
  override def pureDelete(id:Int):Future[Int]                                   = interfaceDAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = interfaceDAO.pureDeleteAll
}
