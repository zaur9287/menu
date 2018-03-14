package models.daos

import org.joda.time.DateTime


import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Interface
import models.caseClasses.InterfaceForms.UpdateInterfaceForm
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[InterfacesDAOImpl])
trait InterfacesDAO {
  def get: Future[Seq[Interface]]
  def create(currRow: Interface): Future[Option[Interface]]
  def pureDelete(id:Int): Future[Int]
  def pureDeleteAll: Future[Int]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(id:Int,updateInterfaceForm: UpdateInterfaceForm): Future[Option[Interface]]
  def findInterfaceByID(id: Int): Future[Option[Interface]]
}

class InterfacesDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends InterfacesDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Interface]] = {
    val query = slickInterfaces.filter(r => r.deleted_at.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toInterfaces).sortBy(_.id)
    )
  }
//bir sətrin yaradılmasıı və yaranmış sətrin geri qaytarılması
  override def create(currRow: Interface): Future[Option[Interface]] = {
    val dBInterface = DBInterface(currRow.id, currRow.name, currRow.description, currRow.createdAt, currRow.updatedAt, None)
    val query = slickInterfaces.returning(slickInterfaces) += dBInterface
    db.run(query).map ( r => Some(r.toInterfaces) )
  }
//id-yə görə birinin silinməsi (seçilən sətr bazan silinir.)
  override def delete(selectedID:Int): Future[Int] = {
    val selectedInterface = slickInterfaces.filter(_.id===selectedID)
    val deleteAction = selectedInterface.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }
//bütün sətirlər bazadan silinir.
  override def deleteAll: Future[Int] = {
    val selectAllInterfaces = slickInterfaces
    val deletingAllAction = selectAllInterfaces.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }
//göndərilmiş məlumatların update olunması. və update olunmuş sətrin geri qaytarılması
  override  def update(id:Int,updateInterfaceForm: UpdateInterfaceForm): Future[Option[Interface]] = {
    val updateQuery = slickInterfaces.filter(c => c.id === id && c.deleted_at.isEmpty)
      .map(c => (c.name, c.desc, c.updated_at))
      .update((updateInterfaceForm.name, Some(updateInterfaceForm.description),  DateTime.now))

    val result = for {
      getUpdatedInterface <- findInterfaceByID(id)
      updateRowCount <- if(getUpdatedInterface.isDefined){ db.run(updateQuery) } else { Future(0) }
    } yield {
      if(updateRowCount > 0){
        getUpdatedInterface.map(c => c.copy(name = updateInterfaceForm.name, description = Some(updateInterfaceForm.description), updatedAt = c.updatedAt))
      } else {None}
    }
    result
 }
//id-yə görə birinin tapılması
  override def findInterfaceByID(id:Int): Future[Option[Interface]] = {
    val query = slickInterfaces.filter(f=>f.id === id && f.deleted_at.isEmpty).result
    db.run(query.headOption).map(_.map(_.toInterfaces))
  }
//id-yə görə birinin silinməsi (bazada silinmə tarixinin update olunması)
  override def pureDelete(id:Int): Future[Int] = {
    val query = slickInterfaces.filter(f=>f.id === id && f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
//bütün sətrlərdə silinmə tarixini update olunması
  override def pureDeleteAll: Future[Int] = {
    val query = slickInterfaces.filter(f=>f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


