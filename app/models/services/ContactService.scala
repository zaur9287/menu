package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.{Client, Contact}
import models.caseClasses.ContactForms.UpdateContactForm
import models.daos.ContactsDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[ContactServiceImpl])
trait ContactService {
  def get                               :Future[Seq[Contact]]
  def create (client: Contact)          :Future[Option[Contact]]
  def pureDelete(id:Int)                :Future[Int]
  def pureDeleteAll                     :Future[Int]
  def delete(id:Int)                    :Future[Int]
  def deleteAll                         :Future[Int]
  def update(id: Int, updateContactForm :UpdateContactForm):Future[Int]
  def findContactByID(id: Int)          :Future[Option[Contact]]
  def findContactByClientID(id: Int)    :Future[Option[(Client, Seq[Contact])]]
}

class ContactServiceImpl @Inject()(contactsDAO: ContactsDAO) extends ContactService {
  def get: Future[Seq[Contact]] = {
    for {
      allContacts <- contactsDAO.get
    } yield {
      allContacts
    }
  }
  override def create(client: Contact): Future[Option[Contact]]                 = contactsDAO.create(client)
  override def delete(selectedID:Int): Future[Int]                              = contactsDAO.delete(selectedID)
  override def deleteAll: Future[Int]                                           = contactsDAO.deleteAll
  override def update(id: Int, updateContactForm: UpdateContactForm): Future[Int] = contactsDAO.update(id, updateContactForm)
  override def findContactByID(id:Int):Future[Option[Contact]]                  = contactsDAO.findContactByID(id)
  override def findContactByClientID(id:Int):Future[Option[(Client, Seq[Contact])]]= contactsDAO.findContactByClientID(id)
  override def pureDelete(id:Int):Future[Int]                                   = contactsDAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                        = contactsDAO.pureDeleteAll
}
