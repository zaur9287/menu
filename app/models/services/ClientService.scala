package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject
import models.caseClasses.Client
import models.daos.ClientsDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[ClientServiceImpl])
trait ClientService {
  def get: Future[Seq[Client]]
  def create(client: Client): Future[Option[Client]]
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
}
