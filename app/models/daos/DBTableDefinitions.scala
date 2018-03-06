package models.daos

import models.caseClasses.Client
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.PostgresProfile

trait DBTableDefinitions extends HasDatabaseConfigProvider[PostgresProfile] {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  case class DBClient(
                     id: Int,
                     name: String,
                     desc: Option[String],
                     expireDate: DateTime,
                     createdAt: DateTime,
                     updatedAt: DateTime,
                     deletedAt: Option[DateTime]
                     ){
    def toClients: Client = Client(id, name, desc, expireDate, createdAt, updatedAt)
    def isExpired: Boolean = DateTime.now().isAfter(expireDate)
  }

  class Clients(tag: Tag) extends Table[DBClient](tag, "clients"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def desc = column[Option[String]]("description")
    def expire_date = column[DateTime]("expiry_date")
    def created_at = column[DateTime]("created_at")
    def updated_at = column[DateTime]("updated_at")
    def deleted_at = column[Option[DateTime]]("deleted_at")

    override def * = (id, name, desc, expire_date, created_at, updated_at, deleted_at) <> (DBClient.tupled, DBClient.unapply)
  }

  val slickClients = TableQuery[Clients]
}
