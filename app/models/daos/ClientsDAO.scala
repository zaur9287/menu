package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Client
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ClientsDAOImpl])
trait ClientsDAO {
  def get: Future[Seq[Client]]
  def create(client: Client): Future[Option[Client]]
}

class ClientsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends ClientsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Client]] = {
    val query = slickClients.filter(r => r.deleted_at.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toClients)
    )
  }

  override def create(client: Client): Future[Option[Client]] = {
    val dBClient = DBClient(client.id, client.name, client.description, client.expireDate, client.createdAt, client.updatedAt, None)
    val query = slickClients.returning(slickClients) += dBClient
    db.run(query).map ( r => Some(r.toClients) )
  }

}


