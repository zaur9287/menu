package models.services
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.caseClasses.{Company, LInfo, User}
import models.daos.{CompanyDAO, UsersDAO}

import scala.concurrent.ExecutionContext.Implicits.global
import com.mohiva.play.silhouette.api.services.IdentityService

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import javax.inject.Inject

import com.google.inject.ImplementedBy
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import org.joda.time.DateTime

@ImplementedBy(classOf[UserServiceImpl])
trait UserService extends IdentityService[User]  {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]]
  def retrieve(userID: UUID)        : Future[Option[User]]
  def save(user: User, company: Company): Future[User]
  def save(profile: CommonSocialProfile): Future[User]
  def update(userID: UUID, user: User): Future[Int]
  def delete(userID: UUID, companyID: Int): Future[Int]
}
class UserServiceImpl @Inject()(usersDAO: UsersDAO, companyDAO: CompanyDAO) extends UserService {
  override def update(userID: UUID, user: User): Future[Int] = usersDAO.update(userID, user)
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] =usersDAO.find(loginInfo)
  override def retrieve(userID: UUID)        : Future[Option[User]] =usersDAO.find(userID)
  override def save(user: User, company: Company): Future[User] = {
    for {
      createdCompany <- companyDAO.createWith(company)
      createdUser <- usersDAO.save(user.copy(companyID = createdCompany.id, owner = true))
    } yield createdUser
  }
  override def save(profile: CommonSocialProfile) = {
    val email = profile.email.toString
    usersDAO.findEmail(email).flatMap {
      case Some(user) => // Update user with profile
        val foundedUser = User(user.userID, profile.loginInfo, profile.fullName.get, user.email.toLowerCase, profile.avatarURL, user.activated, user.createdAt, user.updatedAt, user.companyID, user.owner)
        val testuser = usersDAO.update(user.userID, foundedUser)
        Future(foundedUser)
      case None => // Insert a new user and new login info
        val newUserID = UUID.randomUUID()
        val createdUser = usersDAO.save(User(newUserID, profile.loginInfo, profile.fullName.get, profile.email.get.toLowerCase, profile.avatarURL, true, DateTime.now, DateTime.now, 0, owner = false))
        val newLoginInfo = LInfo(0,LoginInfo(profile.loginInfo.providerID, profile.loginInfo.providerKey), newUserID.toString)
        createdUser
    }
  }

  override def delete(userID: UUID, companyID: Int) = usersDAO.delete(userID, companyID)

}