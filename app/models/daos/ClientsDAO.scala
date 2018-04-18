package models.daos
import org.joda.time.DateTime


import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Client
import models.caseClasses.ClientForms.UpdateClientForm
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ClientsDAOImpl])
trait ClientsDAO {
  def get: Future[Seq[Client]]
  def create(client: Client): Future[Option[Client]]
  def pureDelete(id:Int): Future[Int]
  def pureDeleteAll: Future[Int]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(id:Int,updateClientForm: UpdateClientForm): Future[Int]
  def findClientByID(clientid: Int): Future[Option[Client]]
}

class ClientsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends ClientsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Client]] = {
    val query = slickClients.filter(r => r.deleted_at.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toClients).sortBy(_.id)
    )
  }
//bir sətrin yaradılmasıı və yaranmış sətrin geri qaytarılması
  override def create(client: Client): Future[Option[Client]] = {
    val dBClient = DBClient(client.id, client.name, client.description, client.expireDate, client.createdAt, client.updatedAt, None)
    val query = slickClients.returning(slickClients) += dBClient
    db.run(query).map ( r => Some(r.toClients) )
  }
//id-yə görə birinin silinməsi (seçilən sətr bazan silinir.)
  override def delete(selectedID:Int): Future[Int] = {
    val selectedClient = slickClients.filter(_.id===selectedID)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }
//bütün sətirlər bazadan silinir.
  override def deleteAll: Future[Int] = {
    val selectAllClients = slickClients
    val deletingAllAction = selectAllClients.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override  def update(id:Int,updateClientForm: UpdateClientForm): Future[Int] = {
    val updateQuery = slickClients.filter(c => c.id === id && c.deleted_at.isEmpty)
      .map(c => (c.name, c.desc, c.expire_date, c.updated_at))
      .update((updateClientForm.name, Some(updateClientForm.description), DateTime.parse(updateClientForm.expireDate), DateTime.now))

    db.run(updateQuery)
 }
//id-yə görə birinin tapılması
  override def findClientByID(clientid:Int): Future[Option[Client]] = {
    val query = slickClients.filter(f=>f.id === clientid && f.deleted_at.isEmpty).result
    db.run(query.headOption).map(_.map(_.toClients))
  }
//id-yə görə birinin silinməsi (bazada silinmə tarixinin update olunması)
  override def pureDelete(id:Int): Future[Int] = {
    val query = slickClients.filter(f=>f.id === id && f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
//bütün sətrlərdə silinmə tarixini update olunması
  override def pureDeleteAll: Future[Int] = {
    val query = slickClients.filter(f=>f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


