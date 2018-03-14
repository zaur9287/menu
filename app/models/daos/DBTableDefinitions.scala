package models.daos

import models.caseClasses.{Client,Company,Country,Contact,Interface}
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.PostgresProfile

trait DBTableDefinitions extends HasDatabaseConfigProvider[PostgresProfile] {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  //**************Client table***************************************************************************************
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

  //**************Client contacts table***************************************************************************************
  case class DBContact(
                       id: Int,
                       client_id: Int,
                       mobile: String,
                       desc: String,
                       createdAt: DateTime,
                       updatedAt: DateTime,
                       deletedAt: Option[DateTime]
                     ){
    def toContacts: Contact = Contact(id,client_id, mobile, desc, createdAt, updatedAt)
  }

  class Contacts(tag: Tag) extends Table[DBContact](tag, "contacts"){
    def id            = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def client_id     = column[Int]("client_id")
    def mobile        = column[String]("mobile")
    def desc          = column[String]("description")
    def created_at    = column[DateTime]("created_at")
    def updated_at    = column[DateTime]("updated_at")
    def deleted_at    = column[Option[DateTime]]("deleted_at")

    override def * = (id, client_id, mobile, desc, created_at, updated_at, deleted_at) <> (DBContact.tupled, DBContact.unapply)
  }

  val slickContacts= TableQuery[Contacts]


  //**************Company table***************************************************************************************
  case class DBCompany(
                       id: Int,
                       name: String,
                       desc: Option[String],
                       createdAt: DateTime,
                       updatedAt: DateTime,
                       deletedAt: Option[DateTime]
                     ){
    def toCompanies: Company= Company(id, name, desc, createdAt, updatedAt)
  }

  class Companies(tag: Tag) extends Table[DBCompany](tag, "companies"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def desc = column[Option[String]]("description")
    def created_at = column[DateTime]("created_at")
    def updated_at = column[DateTime]("updated_at")
    def deleted_at = column[Option[DateTime]]("deleted_at")

    override def * = (id, name, desc, created_at, updated_at, deleted_at) <> (DBCompany.tupled, DBCompany.unapply)
  }

  val slickCompanies = TableQuery[Companies]

  //**************Country table***************************************************************************************
  case class DBCountry(
                        id: Int,
                        name: String,
                        desc: Option[String],
                        createdAt: DateTime,
                        updatedAt: DateTime,
                        deletedAt: Option[DateTime]
                      ){
    def toCountries: Country= Country(id, name, desc, createdAt, updatedAt)
  }

  class Countries(tag: Tag) extends Table[DBCountry](tag, "countries"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def desc = column[Option[String]]("description")
    def created_at = column[DateTime]("created_at")
    def updated_at = column[DateTime]("updated_at")
    def deleted_at = column[Option[DateTime]]("deleted_at")

    override def * = (id, name, desc, created_at, updated_at, deleted_at) <> (DBCountry.tupled, DBCountry.unapply)
  }

  val slickCountries = TableQuery[Countries]

  //**************Interface table***************************************************************************************
  case class DBInterface(
                        id: Int,
                        name: String,
                        desc: Option[String],
                        createdAt: DateTime,
                        updatedAt: DateTime,
                        deletedAt: Option[DateTime]
                      ){
    def toInterfaces: Interface= Interface(id, name, desc, createdAt, updatedAt)
  }

  class Interfaces(tag: Tag) extends Table[DBInterface](tag, "interfaces"){
    def id              = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name            = column[String]("name")
    def desc            = column[Option[String]]("description")
    def created_at      = column[DateTime]("created_at")
    def updated_at      = column[DateTime]("updated_at")
    def deleted_at      = column[Option[DateTime]]("deleted_at")

    override def * = (id, name, desc, created_at, updated_at, deleted_at) <> (DBInterface.tupled, DBInterface.unapply)
  }

  val slickInterfaces = TableQuery[Interfaces]



}
