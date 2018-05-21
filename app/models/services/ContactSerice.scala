package models.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.{Contact, ContactForm}
import models.daos.ContactsDAO

import scala.concurrent.Future


@ImplementedBy(classOf[ContactServiceImpl])
trait ContactService {
  def getAll: Future[Seq[Contact]]
  def update(contactID:Int, companyID: Int, userID: Option[String], contactForm: ContactForm): Future[Int]
  def delete(contactID: Int): Future[Int]
  def findByID(contactID: Int): Future[Option[Contact]]
  def create(contactForm: ContactForm): Future[Contact]
}

class ContactServiceImpl @Inject() (contactsDAO: ContactsDAO) extends ContactService {
  override def getAll: Future[Seq[Contact]] = contactsDAO.getAll
  override def update(contactID: Int, companyID: Int, userID: Option[String], contactForm: ContactForm): Future[Int] = contactsDAO.update(contactID, companyID, userID, contactForm)
  override def delete(contactID: Int): Future[Int] = contactsDAO.delete(contactID)
  override def findByID(contactID: Int): Future[Option[Contact]] = contactsDAO.findByID(contactID)
  override def create(contactForm: ContactForm): Future[Contact] = contactsDAO.create(contactForm)
}