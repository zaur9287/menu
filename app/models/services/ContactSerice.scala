package models.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.{Company, Contact, ContactForm, User}
import models.daos.ContactsDAO

import scala.concurrent.Future


@ImplementedBy(classOf[ContactServiceImpl])
trait ContactService {
  def getAll: Future[Seq[Contact]]
  def update(contactID:Int, companyID: Option[Int], userID: Option[String], contactForm: ContactForm): Future[Int]
  def delete(contactID: Int): Future[Int]
  def findByID(contactID: Int): Future[Option[Contact]]
  def create(contactForm: ContactForm): Future[Contact]
  def getUserContact(userID: String): Future[Option[(User, Seq[Contact])]]
  def getCompanyContact(companyID: Int): Future[Option[(Company, Seq[Contact])]]
}

class ContactServiceImpl @Inject() (contactsDAO: ContactsDAO) extends ContactService {
  override def getAll: Future[Seq[Contact]] = contactsDAO.getAll
  override def update(contactID: Int, companyID: Option[Int], userID: Option[String], contactForm: ContactForm): Future[Int] = contactsDAO.update(contactID, companyID, userID, contactForm)
  override def delete(contactID: Int): Future[Int] = contactsDAO.delete(contactID)
  override def findByID(contactID: Int): Future[Option[Contact]] = contactsDAO.findByID(contactID)
  override def create(contactForm: ContactForm): Future[Contact] = contactsDAO.create(contactForm)
  override def getUserContact(userID: String): Future[Option[(User, Seq[Contact])]] = contactsDAO.getUserContact(userID)

  override def getCompanyContact(companyID: Int): Future[Option[(Company, Seq[Contact])]] = contactsDAO.getCompanyContact(companyID)
}