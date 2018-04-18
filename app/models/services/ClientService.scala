package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.Client
import models.caseClasses.ClientForms.UpdateClientForm
import models.daos.ClientsDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[ClientServiceImpl])
trait ClientService {
  def get                               :Future[Seq[Client]]
  def create (client: Client)           :Future[Option[Client]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(selectedID:Int)            :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, updateClientForm  :UpdateClientForm):Future[Int]
  def findClientByID(clientid: Int)     :Future[Option[Client]]
}

class ClientServiceImpl @Inject()(clientsDAO: ClientsDAO) extends ClientService {
  override def get: Future[Seq[Client]] = {
    for {
      allClients <- clientsDAO.get
    } yield {
      allClients
    }
  }
  override def create(client: Client): Future[Option[Client]]                   = clientsDAO.create(client)
  override def delete(selectedID:Int): Future[Int]                              = clientsDAO.delete(selectedID)
  override def deleteAll: Future[Int]                                           = clientsDAO.deleteAll
  override def update(id: Int, updateClientForm: UpdateClientForm): Future[Int] = clientsDAO.update(id, updateClientForm)
  override def findClientByID(clientid:Int):Future[Option[Client]]              = clientsDAO.findClientByID(clientid)
  override def pureDelete(id:Int):Future[Int]                                   = clientsDAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = clientsDAO.pureDeleteAll
}
