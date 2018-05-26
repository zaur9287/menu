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
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  def find(id: UUID) = {
    val query = slickTokens.filter(_.id === id.toString)
    db.run(query.result.headOption).map(r =>
      if(r.isDefined)
        Some(AuthToken(UUID.fromString(r.get.ID), UUID.fromString(r.get.userID), r.get.expiry))
      else
        None
    )
  }

  def findExpired(dateTime: DateTime) = {
    val query = slickTokens.filter(_.expiry < dateTime)
    for {
      seq <- db.run(query.result)
    } yield seq.map(r => AuthToken(UUID.fromString(r.ID), UUID.fromString(r.userID), r.expiry))
  }

  def save(token: AuthToken) = {
    val dBToken = DBToken(token.id.toString, token.userID.toString, token.expiry)
    val insertQuery = slickTokens.returning(slickTokens) += dBToken
    db.run(insertQuery).map(r => token)
  }

  def remove(id: UUID) = {
    val query = slickTokens.filter(_.id === id.toString)
    db.run(query.delete)
    Future.successful(())
  }

}

