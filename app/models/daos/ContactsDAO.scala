package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Contact, ContactForm}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ContactsDAOImpl])
trait ContactsDAO {
  def getAll: Future[Seq[Contact]]
  def update(contactID:Int, companyID: Int, userID: String, contactForm: ContactForm): Future[Int]
  def delete(contactID: Int): Future[Int]
  def findByID(contactID: Int): Future[Option[Contact]]
  def create(contactForm: ContactForm): Future[Contact]
}

class ContactsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ContactsDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Contact]] = {
    val getQuery = slickContacts.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toContact)) } yield all
  }

  override def update(contactID:Int, companyID: Int, userID: String, contactForm: ContactForm): Future[Int] = {
    var updateQuery = slickContacts.filter(f => f.deleted === false && f.id ===contactID && f.companyID === companyID && f.userID === userID)
      .map(u => (u.property, u.value, u.userID, u.companyID))
      .update((contactForm.property, contactForm.value, contactForm.userID, contactForm.companyID))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(contactID: Int): Future[Int] = {
    val deleteQuery = slickContacts.filter(_.id === contactID)
      .map(c => (c.updatedAt, c.deleted)).update((DateTime.now, true))
    for { deleted <- db.run(deleteQuery).map(r => r) } yield deleted
  }

  override def findByID(contactID: Int): Future[Option[Contact]] = {
    val findQuery = slickContacts.filter(f => f.deleted === false && f.id === contactID)
    for { result <- db.run(findQuery.result.headOption).map(r => if (r.isDefined) Some(r.get.toContact) else None ) } yield result
  }

  override def create(contactForm: ContactForm): Future[Contact] = {
    val dBContact = DBContacts(0, contactForm.property, contactForm.value, contactForm.userID, contactForm.companyID, DateTime.now(), DateTime.now(), false)
    val insertQuery = slickContacts.returning(slickContacts) += dBContact
    for { newRow <- db.run(insertQuery).map(r => r.toContact) } yield newRow
  }
}