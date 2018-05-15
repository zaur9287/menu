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
                      activated: Boolean
                    ){
    def toUser:User = User(UUID.fromString(userID),LoginInfo("credentials",email),fullName,email,avatarURL,activated)

  }

  class Users(tag: Tag) extends Table[DBUser](tag, "users") {
    def id          = column[String]("userid", O.PrimaryKey)
    def fullName    = column[String]("fullname")
    def email       = column[String]("email")
    def avatarURL   = column[Option[String]]("avatarurl")
    def activated   = column[Boolean]       ("activated")
    def * = (id, fullName, email, avatarURL,activated) <> (DBUser.tupled, DBUser.unapply)
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


  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickTokens = TableQuery[Tokens]
  val slickPasswordInfos = TableQuery[PasswordInfos]




}
