package models.daos

import java.util.UUID

import com.google.inject.{ImplementedBy, Inject}
import com.mohiva.play.silhouette.api.LoginInfo
import models.caseClasses.User
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[UsersDAOImpl])
trait UsersDAO {
  def find(loginInfo: LoginInfo): Future[Option[User]]
  def find(userID: UUID): Future[Option[User]]
  def findEmail(email: String): Future[Option[User]]
  def save(user: User): Future[User]
  def update(id:String,user: User): Future[Int]
}

class UsersDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends UsersDAO with DBTableDefinitions {

  import profile.api._

  val users: mutable.HashMap[UUID, User] = mutable.HashMap()

  override def find(loginInfo: LoginInfo) = {
    val query  = slickUsers.filter(u=>u.email ===loginInfo.providerKey.toLowerCase).result.headOption
    db.run(query).map(u=>u.map(_.toUser))
  }

  override def find(id: UUID): Future[Option[User]] = {
    val query = slickUsers.filter(u => u.id === id.toString).result
    db.run(query.headOption).map(_.map(r=>User(id,LoginInfo("",""),r.fullName,r.email,r.avatarURL,true)))
}
  override def findEmail(email: String): Future[Option[User]] = {
    val query = slickUsers.filter(u => u.email === email.toLowerCase).result
    db.run(query.headOption).map(_.map(r=>User(UUID.fromString(r.userID),LoginInfo("credentials",r.email),r.fullName,r.email,r.avatarURL,true)))
}

  override def save(user: User) = {
    val dbUser  = DBUser(user.userID.toString, user.fullName, user.email, user.avatarURL,user.activated)
    val createUserQuery = slickUsers.returning(slickUsers) += dbUser
    val createLoginInfoQuery = slickLoginInfos += DBLoginInfos(0, "credentials", user.email.toLowerCase, user.userID.toString)
    for {
      createUser <- db.run(createUserQuery)
      createLoginInfo <- db.run(createLoginInfoQuery)
    } yield {
      user
    }
  }


  override  def update(id:String,user: User): Future[Int] = {
    val updateQuery = slickUsers.filter(u => u.id === id)
      .map(u => (u.fullName, u.avatarURL,u.activated))
      .update((user.fullName, user.avatarURL,user.activated))
    db.run(updateQuery)
  }



}


