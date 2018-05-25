package models.daos

import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Company, Contact, ContactForm, User}
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ContactsDAOImpl])
trait ContactsDAO {
  def getAll: Future[Seq[Contact]]
  def update(contactID:Int, companyID: Option[Int], userID: Option[String], contactForm: ContactForm): Future[Int]
  def delete(contactID: Int): Future[Int]
  def findByID(contactID: Int): Future[Option[Contact]]
  def create(contactForm: ContactForm): Future[Contact]
  def getUserContact(userID: String): Future[Option[(User, Seq[Contact])]]
  def getCompanyContact(companyID: Int): Future[Option[(Company, Seq[Contact])]]
}

class ContactsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends ContactsDAO with DBTableDefinitions{
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def getAll: Future[Seq[Contact]] = {
    val getQuery = slickContacts.filter(_.deleted === false)
    for { all <- db.run(getQuery.result).map(_.map(r => r.toContact)) } yield all
  }

  override def update(contactID: Int, companyID: Option[Int], userID: Option[String], contactForm: ContactForm): Future[Int] = {
    var query = slickContacts.filter(f =>f.id === contactID && f.deleted === false)
    companyID.foreach(f => query.filter(_.companyID === f))
    userID.foreach(f => query.filter(_.userID === f))
    var updateQuery = query.map(u => (u.property, u.value, u.userID, u.companyID))
      .update((contactForm.property, contactForm.value, contactForm.userID, contactForm.companyID))
    for { updated <- db.run(updateQuery).map(r => r)} yield updated
  }

  override def delete(contactID: Int): Future[Int] = {
    val deleteQuery = slickContacts.filter(f => f.deleted === false && f.id === contactID)
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


  override def getUserContact(userID: String): Future[Option[(User, Seq[Contact])]] = {
    val query = slickContacts.filter(f => f.userID === userID && f.deleted === false)
      .join(slickUsers.filter(f => f.id === userID && f.deleted === false).take(1)).on(_.userID === _.id).sortBy(_._1.id)
    for {
      contacts <- db.run(query.result)
    } yield
      contacts.groupBy(_._2).headOption.map(grouped => {
        val (user, seqContacts) = grouped
        (user.toUser, seqContacts.map(c => c._1.toContact))
      })
  }

  override def getCompanyContact(companyID: Int): Future[Option[(Company, Seq[Contact])]] = {
    val query = slickContacts.filter(f => f.companyID === companyID && f.deleted === false)
      .join(slickCompanies.filter(f => f.id ===companyID && f.deleted === false)).sortBy(_._1.id)

    for {
      contacts <- db.run(query.result)
    } yield {
      contacts.groupBy(_._2).headOption.map(grouped =>{
        val (company, seqContact) = grouped
        (company.toCompany, seqContact.map(contact => contact._1.toContact))
      })
    }
  }
}

