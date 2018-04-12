package models.daos

import java.util.UUID

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.AuthToken
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[TokensDAOImpl])
trait TokensDAO {
  def find(id: UUID): Future[Option[AuthToken]]
  def findExpired(dateTime: DateTime): Future[Seq[AuthToken]]
  def save(token: AuthToken): Future[AuthToken]
  def remove(id: UUID): Future[Unit]
}

class TokensDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends TokensDAO with DBTableDefinitions {
  val tokens: mutable.HashMap[UUID, AuthToken] = mutable.HashMap()

  def find(id: UUID) = Future.successful(tokens.get(id))

  def findExpired(dateTime: DateTime) = Future.successful {
    tokens.filter {
      case (_, token) =>
        token.expiry.isBefore(dateTime)
    }.values.toSeq
  }
  def save(token: AuthToken) = {
    tokens += (token.id -> token)
    Future.successful(token)
  }
  def remove(id: UUID) = {
    tokens -= id
    Future.successful(())
  }

}


