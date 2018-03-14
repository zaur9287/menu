package models.daos

import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Client, Contact}
import models.caseClasses.ContactForms.UpdateContactForm
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ContactsDAOImpl])
trait ContactsDAO {
  def get: Future[Seq[Contact]]
  def create(contact: Contact): Future[Option[Contact]]
  def pureDelete(id:Int): Future[Int]
  def pureDeleteAll: Future[Int]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(id:Int,updateselectAllContactsForm: UpdateContactForm): Future[Option[Contact]]
  def findContactByID(id: Int): Future[Option[Contact]]
  def findContactByClientID(id: Int): Future[Option[(Client, Seq[Contact])]]
}

class ContactsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends ContactsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Contact]] = {
    val query = slickContacts.filter(r => r.deleted_at.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toContacts).sortBy(_.id)
    )
  }
//bir sətrin yaradılmasıı və yaranmış sətrin geri qaytarılması
  override def create(contact: Contact): Future[Option[Contact]] = {
    val dBContact = DBContact(contact.id, contact.client_id,contact.mobile, contact.description, contact.createdAt, contact.updatedAt, None)
    val query = slickContacts.returning(slickContacts) += dBContact
    db.run(query).map ( r => Some(r.toContacts) )
  }
//id-yə görə birinin silinməsi (seçilən sətr bazan silinir.)
  override def delete(selectedID:Int): Future[Int] = {
    val selectedContact = slickContacts.filter(_.id===selectedID)
    val deleteAction = selectedContact.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }
//bütün sətirlər bazadan silinir.
  override def deleteAll: Future[Int] = {
    val selectAllContacts = slickContacts
    val deletingAllAction = selectAllContacts.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }
//göndərilmiş məlumatların update olunması. və update olunmuş sətrin geri qaytarılması
  override def update(id:Int,updateContactForm: UpdateContactForm): Future[Option[Contact]] = {
    val updateQuery = slickContacts.filter(c => c.id === id && c.deleted_at.isEmpty)
      .map(c => (c.client_id ,c.mobile, c.desc, c.updated_at))
      .update((updateContactForm.client_id, updateContactForm.mobile,updateContactForm.description, DateTime.now))

    val result = for {
      getUpdatedContact <- findContactByID(id)
      updateRowCount <- if(getUpdatedContact.isDefined){ db.run(updateQuery) } else { Future(0) }
    } yield {
      if(updateRowCount > 0){
        getUpdatedContact.map(c => c.copy(client_id = updateContactForm.client_id, mobile = updateContactForm.mobile, description = updateContactForm.description, updatedAt = c.updatedAt))
      } else {None}
    }
    result
 }
//id-yə görə birinin tapılması
  override def findContactByID(id:Int): Future[Option[Contact]] = {
    val query = slickContacts.filter(f=>f.id === id && f.deleted_at.isEmpty).result
    db.run(query.headOption).map(_.map(_.toContacts))
  }
//client id-yə görə contact-larin tapılması
  override def findContactByClientID(id:Int): Future[Option[(Client, Seq[Contact])]] = {
    val query = slickClients.filter(c => c.id === id && c.deleted_at.isEmpty)
      .joinLeft(slickContacts.filter(_.deleted_at.isEmpty)).on(_.id === _.client_id)

    for {
      clientAndContacts <- db.run(query.result)
    } yield {
      val contacts = clientAndContacts.map(_._2).flatten.map(_.toContacts)
      if(clientAndContacts.length > 0){
        Some(clientAndContacts.head._1.toClients, contacts)
      } else {
        None
      }
    }

//    val query = slickContacts.filter(f=>f.client_id === id && f.deleted_at.isEmpty).result
//    db.run(query).map(_.map(r=>r.toContacts))
  }
//id-yə görə birinin silinməsi (bazada silinmə tarixinin update olunması)
  override def pureDelete(id:Int): Future[Int] = {
    val query = slickContacts.filter(f=>f.id === id && f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
//bütün sətrlərdə silinmə tarixini update olunması
  override def pureDeleteAll: Future[Int] = {
    val query = slickContacts.filter(f=>f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


