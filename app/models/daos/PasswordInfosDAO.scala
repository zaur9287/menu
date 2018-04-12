package models.daos

import com.google.inject.{ImplementedBy, Inject}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

class PasswordInfosDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends DelegableAuthInfoDAO[PasswordInfo] with DBTableDefinitions {
  import profile.api._

  protected def passwordInfoQuery(loginInfo: LoginInfo) = for {
    dbLoginInfo <- loginInfoQuery(loginInfo)
    dbPasswordInfo <- slickPasswordInfos if dbPasswordInfo.logininfoID === dbLoginInfo.id
  } yield dbPasswordInfo

  // Use subquery workaround instead of join to get authinfo because slick only supports selecting
  // from a single table for update/delete queries (https://github.com/slick/slick/issues/684).
  protected def passwordInfoSubQuery(loginInfo: LoginInfo) = {
    val test = loginInfoQuery(loginInfo).map(_.id)
    slickPasswordInfos.filter(_.logininfoID in test)
  }

  protected def addAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    loginInfoQuery(loginInfo).result.head.flatMap { dbLoginInfo =>
        slickPasswordInfos +=
          DBPasswordInfos(0,dbLoginInfo.id,authInfo.hasher, authInfo.password, authInfo.salt)
    }.transactionally

  protected def updateAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    passwordInfoSubQuery(loginInfo).
      map(dbPasswordInfo => (dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt)).
      update((authInfo.hasher, authInfo.password, authInfo.salt))

  protected def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerid === loginInfo.providerID && dbLoginInfo.providerkey.toLowerCase === loginInfo.providerKey.toLowerCase)

  /**
    * Finds the auth info which is linked with the specified login info.
    *
    * @param loginInfo The linked login info.
    * @return The retrieved auth info or None if no auth info could be retrieved for the given login info.
    */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    db.run(passwordInfoQuery(loginInfo).result.headOption).map { dbPasswordInfoOption =>
      dbPasswordInfoOption.map(dbPasswordInfo =>
        PasswordInfo(dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))
    }
  }

  /**
    * Adds new auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be added.
    * @param authInfo The auth info to add.
    * @return The added auth info.
    */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  /**
    * Updates the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be updated.
    * @param authInfo The auth info to update.
    * @return The updated auth info.
    */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  /**
    * Saves the auth info for the given login info.
    *
    * This method either adds the auth info if it doesn't exists or it updates the auth info
    * if it already exists.
    *
    * @param loginInfo The login info for which the auth info should be saved.
    * @param authInfo The auth info to save.
    * @return The saved auth info.
    */
  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val query = loginInfoQuery(loginInfo).joinLeft(slickPasswordInfos).on(_.id === _.logininfoID)
    val action = query.result.head.flatMap {
      case (dbLoginInfo, Some(dbPasswordInfo)) => updateAction(loginInfo, authInfo)
      case (dbLoginInfo, None) => addAction(loginInfo, authInfo)
    }
    db.run(action).map(_ => authInfo)
  }

  /**
    * Removes the auth info for the given login info.
    *
    * @param loginInfo The login info for which the auth info should be removed.
    * @return A future to wait for the process to be completed.
    */
  def remove(loginInfo: LoginInfo): Future[Unit] =
    db.run(passwordInfoSubQuery(loginInfo).delete).map(_ => ())


}


