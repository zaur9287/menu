package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.caseClasses._
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.PostgresProfile
import slick.lifted.ProvenShape
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions extends HasDatabaseConfigProvider[PostgresProfile] {
  import com.github.tototoshi.slick.PostgresJodaSupport._
  import profile.api._
  //result count for per page
  var resultCount:Int = 20
  def calculateMaxPageNum(all:Int):Int = if (all%resultCount>0) all/resultCount+1 else all/resultCount

  case class DBUser (
                      userID: String,
                      fullName: String,
                      email: String,
                      avatarURL: Option[String],
                      activated: Boolean,
                      createdAt: DateTime,
                      updatedAt: DateTime,
                      deletedAt: Boolean
                    ){
    def toUser:User = User(UUID.fromString(userID), LoginInfo("credentials", email), fullName, email, avatarURL, activated, createdAt, updatedAt)

  }

  class Users(tag: Tag) extends Table[DBUser](tag, "users") {
    def id          = column[String]("userid", O.PrimaryKey)
    def fullName    = column[String]("fullname")
    def email       = column[String]("email")
    def avatarURL   = column[Option[String]]("avatarurl")
    def activated   = column[Boolean] ("activated")
    def createdAt   = column[DateTime]("created_at")
    def updatedAt   = column[DateTime]("updated_at")
    def deleted     = column[Boolean]("deleted")
    def * = (id, fullName, email, avatarURL, activated, createdAt, updatedAt, deleted) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBToken (
                      ID: String,
                      userID: String,
                      expiry: DateTime
                      )

  class Tokens(tag: Tag) extends Table[DBToken](tag, "tokens") {
    def id          = column[String]("id", O.PrimaryKey, O.AutoInc)
    def userID      = column[String]("userid")
    def expiry      = column[DateTime]("expiry")
    def * = (id, userID, expiry) <> (DBToken.tupled, DBToken.unapply)
  }

  case class DBLoginInfos(
                          id          :Int,
                          providerid  : String,
                          providerkey : String,
                          userid      : String
                        ){
    def toLInfo: LInfo= LInfo(id,LoginInfo(providerid,providerkey),userid)
  }

  class LoginInfos(tag: Tag) extends Table[DBLoginInfos](tag, "logininfo"){
    def id                       = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def providerid               = column[String]("providerid")
    def providerkey              = column[String]("providerkey")
    def userid                   = column[String]("userid")

    override def * = (id,providerid, providerkey, userid) <> (DBLoginInfos.tupled, DBLoginInfos.unapply)
  }

  case class DBPasswordInfos(
                           id           : Int,
                           logininfoID  : Int,
                           hasher       : String,
                           password     : String,
                           salt         : Option[String]
                         ){
    def toPInfo: PInfo= PInfo(id,logininfoID,PasswordInfo(hasher,password,salt))
  }

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfos](tag, "passwordinfo"){
    def id              = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def logininfoID     = column[Int]("logininfo_id")
    def hasher          = column[String]("hasher")
    def password        = column[String]("password")
    def salt            = column[Option[String]]("salt")

    override def * = (id,logininfoID,hasher,password,salt) <> (DBPasswordInfos.tupled, DBPasswordInfos.unapply)
  }

  case class DBCompanies(
                    id: Int,
                    name: String,
                    description: Option[String],
                    imageID: Int,
                    createdAt: DateTime,
                    updatedAt: DateTime,
                    deletedAt: Boolean
                    ){

  def toCompany = Company(id, name, description, imageID, createdAt, updatedAt)
  }

  class Companies(tag: Tag) extends Table[DBCompanies](tag, "company"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[Option[String]]("description")
    def imageID = column[Int]("image_id")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")
    def deleted = column[Boolean]("deleted")

    override def * = (id, name, description, imageID, createdAt, updatedAt, deleted) <> (DBCompanies.tupled, DBCompanies.unapply)
  }

  case class DBContacts(
                       id: Int,
                       property: String,
                       value: String,
                       userID: Option[String],
                       companyID: Option[Int],
                       createdAt: DateTime,
                       updatedAt: DateTime,
                       deleted: Boolean
                       ){
    def toContact = Contact(id, property, value, userID, companyID, createdAt, updatedAt)
  }

 class Contacts (tag: Tag) extends Table[DBContacts](tag, "contacts"){
   def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
   def property = column[String]("property")
   def value = column[String]("value")
   def userID = column[Option[String]]("user_id")
   def companyID = column[Option[Int]]("company_id")
   def createdAt = column[DateTime]("created_at")
   def updatedAt = column[DateTime]("updated_at")
   def deleted = column[Boolean]("deleted")

   override def * = (id, property, value, userID, companyID, createdAt, updatedAt, deleted) <> (DBContacts.tupled, DBContacts.unapply)
 }


  case class DBJobs (
                    id: Int,
                    userID: String,
                    companyID: Int,
                    roleID: Int,
                    name: String,
                    description: Option[String],
                    createdAt: DateTime,
                    updatedAt: DateTime,
                    deleted: Boolean
                  ){
    def toJob = Job(id, userID, companyID, roleID, name, description, createdAt, updatedAt)
  }

  class Jobs (tag: Tag) extends Table[DBJobs](tag, "jobs"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def userID = column[String]("user_id")
    def companyID = column[Int]("company_id")
    def roleID = column[Int]("role_id")
    def name = column[String]("name")
    def description = column[Option[String]]("description")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")
    def deleted = column[Boolean]("deleted")

    override def * = (id, userID, companyID, roleID, name, description, createdAt, updatedAt, deleted) <> (DBJobs.tupled, DBJobs.unapply)
  }


  case class DBRoles (
                     id: Int,
                     name: String,
                     createdAt: DateTime,
                     updatedAt: DateTime,
                     deleted: Boolean
                     ){
    def toRole = Role(id, name, createdAt, updatedAt)
  }

  class Roles (tag: Tag) extends Table[DBRoles](tag, "role"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")
    def deleted = column[Boolean]("deleted")

    override def * = (id, name, createdAt, updatedAt, deleted) <> (DBRoles.tupled, DBRoles.unapply)
  }

  case class DBImages(
                   id: Int,
                   path: String,
                   createdAt: DateTime,
                   updatedAt: DateTime,
                   deleted: Boolean
                   ){
    def toImage = Image(id, path, createdAt, updatedAt)
  }
  class Images (tag: Tag) extends Table[DBImages](tag, "images"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def path = column[String]("path")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")
    def deleted = column[Boolean]("deleted")

    override def * = (id, path, createdAt, updatedAt, deleted) <> (DBImages.tupled, DBImages.unapply)
  }

  case class DBTables (
                      id: Int,
                      companyID: Int,
                      name: String,
                      description: Option[String],
                      imageID: Int,
                      createdAt: DateTime,
                      updatedAt: DateTime,
                      deleted: Boolean
                      ){
    def toTable = Table_(id, companyID, name, description, imageID, createdAt, updatedAt)
  }

  class Tables (tag: Tag) extends Table[DBTables](tag, "table_"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def companyID = column[Int]("company_id")
    def name = column[String]("name")
    def description = column[Option[String]]("description")
    def imageID = column[Int]("image_id")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")
    def deleted = column[Boolean]("deleted")

    override def * = (id, companyID, name, description, imageID, createdAt, updatedAt, deleted) <> (DBTables.tupled, DBTables.unapply)
  }


  case class DBGoodGroups(
                         id: Int,
                         parentID: Option[Int],
                         name: String,
                         imageID: Int,
                         createdAt: DateTime,
                         updatedAt: DateTime,
                         deleted: Boolean
                         ){
    def toGoodGroup = GoodGroup(id, parentID, name, imageID, createdAt, updatedAt)
  }

  class GoodGroups (tag: Tag) extends Table[DBGoodGroups](tag, "good_groups"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def parentID = column[Option[Int]]("parent_id")
    def name = column[String]("name")
    def imageID = column[Int]("image_id")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")
    def deleted = column[Boolean]("deleted")

    override def * = (id, parentID, name, imageID, createdAt, updatedAt, deleted) <> (DBGoodGroups.tupled, DBGoodGroups.unapply)
  }

  case class DBGoods (
                     id: Int,
                     groupID: Int,
                     companyID: Int,
                     name: String,
                     description: Option[String],
                     price: Double,
                     quantity: Double,
                     imageID: Int,
                     createdAt: DateTime,
                     updatedAt: DateTime,
                     deleted: Boolean
                     ){
    def toGood = Good(id, groupID, companyID, name, description, price, quantity, imageID, createdAt, updatedAt)
  }

  class Goods (tag: Tag) extends Table[DBGoods](tag, "goods") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def groupID = column[Int]("group_id")
    def companyID = column[Int]("company_id")
    def name = column[String]("name")
    def description = column[Option[String]]("description")
    def price = column[Double]("price")
    def quantity = column[Double]("quantity")
    def imageID = column[Int]("image_id")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")
    def deleted = column[Boolean]("deleted")

    override def * = (id, groupID, companyID, name, description, price, quantity, imageID, createdAt, updatedAt, deleted) <> (DBGoods.tupled, DBGoods.unapply)
  }

  case class DBOrder (
                     id: Int,
                     tableID: Int,
                     companyID: Int,
                     userID: String,
                     status: Option[String],
                     createdAt: DateTime,
                     updatedAt: DateTime,
                     deleted: Boolean
                     ){
    def toOrder = Order(id, tableID, companyID, userID, status, createdAt, updatedAt)
  }
  class Orders (tag: Tag) extends Table[DBOrder](tag, "order_"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def tableID = column[Int]("table_id")
    def companyID = column[Int]("company_id")
    def userID = column[String]("user_id")
    def status = column[Option[String]]("status")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")
    def deleted = column[Boolean]("deleted")

    override def * = (id, tableID, companyID, userID, status, createdAt, updatedAt, deleted) <> (DBOrder.tupled, DBOrder.unapply)
  }



  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickTokens = TableQuery[Tokens]
  val slickPasswordInfos = TableQuery[PasswordInfos]

  val slickCompanies = TableQuery[Companies]
  val slickContacts = TableQuery[Contacts]
  val slickJobs = TableQuery[Jobs]
  val slickRoles = TableQuery[Roles]
  val slickImages = TableQuery[Images]
  val slickTables = TableQuery[Tables]
  val slickGoodGroups = TableQuery[GoodGroups]
  val slickGoods = TableQuery[Goods]
  val slickOrders = TableQuery[Orders]
}
