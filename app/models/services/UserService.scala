package models.services
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.caseClasses.{LInfo, User}
import models.daos.UsersDAO

import scala.concurrent.ExecutionContext.Implicits.global
import com.mohiva.play.silhouette.api.services.IdentityService

import scala.concurrent.Future
import javax.inject.Inject

import com.google.inject.ImplementedBy
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import org.joda.time.DateTime

@ImplementedBy(classOf[UserServiceImpl])
trait UserDAO extends IdentityService[User]  {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]]
  def retrieve(userID: UUID)        : Future[Option[User]]
  def save(user: User)          : Future[User]
  def save(profile: CommonSocialProfile): Future[User]
}
class UserServiceImpl @Inject()(usersDAO: UsersDAO) extends UserDAO {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] =usersDAO.find(loginInfo)
  override def retrieve(userID: UUID)        : Future[Option[User]] =usersDAO.find(userID)
  override def save(user: User)              : Future[User]         =usersDAO.save(user)
  override def save(profile: CommonSocialProfile) = {
    val pp = profile.email.toString
    usersDAO.findEmail(pp).flatMap {
      case Some(user) => // Update user with profile
        val foundedUser = User(user.userID, profile.loginInfo, profile.fullName.get, user.email.toLowerCase, profile.avatarURL, user.activated, user.createdAt, user.updatedAt)
        val testuser = usersDAO.update(user.userID.toString, foundedUser)
        Future(foundedUser)
      case None => // Insert a new user and new login info
        val newUserID = UUID.randomUUID()
        val createdUser = usersDAO.save(User(newUserID, profile.loginInfo, profile.fullName.get, profile.email.get.toLowerCase, profile.avatarURL, true, DateTime.now, DateTime.now))
        val newLoginInfo = LInfo(0,LoginInfo(profile.loginInfo.providerID, profile.loginInfo.providerKey), newUserID.toString)
        createdUser
    }
  }

}