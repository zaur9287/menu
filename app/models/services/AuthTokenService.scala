package models.services

import java.util.UUID

import models.caseClasses.AuthToken
import models.daos.TokensDAO
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import com.mohiva.play.silhouette.api.util.Clock
import com.google.inject.{ImplementedBy, Inject}
import org.joda.time.DateTimeZone
import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[AuthTokenDAOImpl])
trait AuthTokenDAO {
  /**
    * Creates a new auth token and saves it in the backing store.
    *
    * @param userID The user ID for which the token should be created.
    * @param expiry The duration a token expires.
    * @return The saved auth token.
    */
  def create(userID: UUID, expiry: FiniteDuration = 5 minutes): Future[AuthToken]

  /**
    * Validates a token ID.
    *
    * @param id The token ID to validate.
    * @return The token if it's valid, None otherwise.
    */
  def validate(id: UUID): Future[Option[AuthToken]]

  /**
    * Cleans expired tokens.
    *
    * @return The list of deleted tokens.
    */
  def clean: Future[Seq[AuthToken]]
}


class AuthTokenDAOImpl @Inject()(authTokenDAO: TokensDAO,
                             clock: Clock) extends AuthTokenDAO {
  /**
    * Creates a new auth token and saves it in the backing store.
    *
    * @param userID The user ID for which the token should be created.
    * @param expiry The duration a token expires.
    * @return The saved auth token.
    */
  def create(userID: UUID, expiry: FiniteDuration = 30 minutes) = {
    val token = AuthToken(UUID.randomUUID(), userID, clock.now.withZone(DateTimeZone.UTC).plusSeconds(expiry.toSeconds.toInt))
    authTokenDAO.save(token)
  }

  /**
    * Validates a token ID.
    *
    * @param id The token ID to validate.
    * @return The token if it's valid, None otherwise.
    */
  def validate(id: UUID) = authTokenDAO.find(id)

  /**
    * Cleans expired tokens.
    *
    * @return The list of deleted tokens.
    */
  def clean = authTokenDAO.findExpired(clock.now.withZone(DateTimeZone.UTC)).flatMap { tokens =>
    Future.sequence(tokens.map { token =>
      authTokenDAO.remove(token.id).map(_ => token)
    })
  }

}