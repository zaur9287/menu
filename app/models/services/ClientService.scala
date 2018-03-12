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
  def get: Future[Seq[Client]]
  def create(client: Client): Future[Option[Client]]
  def delete(selectedID:Int):Future[Int]
  def deleteAll:Future[Int]
  def update(id: Int, updateClientForm: UpdateClientForm):Future[Option[Client]]
  def findClientByID(clientid: Int): Future[Option[Client]]
}

class ClientServiceImpl @Inject()(clientsDAO: ClientsDAO) extends ClientService {
  override def get: Future[Seq[Client]] = {
    for {
      allClients <- clientsDAO.get
    } yield {
      allClients
    }
  }
  override def create(client: Client): Future[Option[Client]] = clientsDAO.create(client)
  override def delete(selectedID:Int): Future[Int] = clientsDAO.delete(selectedID)
  override def deleteAll: Future[Int] = clientsDAO.deleteAll
  override def update(id: Int, updateClientForm: UpdateClientForm): Future[Option[Client]] = clientsDAO.update(id, client)
  override def findClientByID(clientid:Int):Future[Option[Client]] = clientsDAO.findClientByID(clientid)
}
