package models.daos
import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Participant
import models.caseClasses.Participant._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ParticipantsDAOImpl])
trait ParticipantsDAO {
  def get: Future[Seq[Participant]]
  def create(row: Participant): Future[Option[Participant]]
  def pureDelete(id:Int): Future[Int]
  def pureDeleteAll: Future[Int]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(id:Int,updateForm: UpdateFormParticipant): Future[Int]
  def findByID(id: Int): Future[Option[Participant]]
  def findByCategoryID(id: Int): Future[Seq[Participant]]
}

class ParticipantsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends ParticipantsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Participant]] = {
    val query = slickParticipants.filter(r => r.deletedAt.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toParticipant).sortBy(_.id)
    )
  }

  override def create(row: Participant): Future[Option[Participant]] = {
    val result = for{
      aff<-db.run(slickParticipants.filter(f=>f.deletedAt.isEmpty && f.categoryID === row.categoryID && f.name ===row.name && f.phone ===row.phone).result)
    }yield{
      if(aff.length==0){
        for{
          res<-db.run(slickParticipants.returning(slickParticipants) +=DBParticipants(row.id, row.name,row.phone,row.company,row.categoryID, row.createdAt, row.updatedAt, None))
        }yield{
          Some(res.toParticipant)
        }
      }else{Future(None)}
    }
    //result.flatten.map(_)
    result.flatMap(r=>r)

//    val dBRow = DBParticipants(row.id, row.name,row.phone,row.company,row.categoryID, row.createdAt, row.updatedAt, None)
//    val query = slickParticipants.returning(slickParticipants) += dBRow
//    db.run(query).map ( r => Some(r.toParticipant) )
  }

  override def delete(selectedID:Int): Future[Int] = {
    val selectedClient = slickParticipants.filter(_.id===selectedID)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def deleteAll: Future[Int] = {
    val selectAllClients = slickParticipants
    val deletingAllAction = selectAllClients.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override  def update(id:Int,updateForm: UpdateFormParticipant): Future[Int] = {
    val updateQuery = slickParticipants.filter(c => c.id === id && c.deletedAt.isEmpty)
      .map(c => (c.name,c.phone,c.company,c.categoryID, c.updatedAt))
      .update((updateForm.name,updateForm.phone,updateForm.company,updateForm.categoryID, DateTime.now))
    db.run(updateQuery)
  }

  override def findByID(clientid:Int): Future[Option[Participant]] = {
    val query = slickParticipants.filter(f=>f.id === clientid && f.deletedAt.isEmpty).result
    db.run(query.headOption).map(_.map(_.toParticipant))
  }

  override def findByCategoryID(id: Int): Future[Seq[Participant]] = {
    val query  = slickParticipants.filter(f=>f.deletedAt.isEmpty && f.categoryID ===id).result
    db.run(query).map(_.map(r=>r.toParticipant))
  }
  override def pureDelete(id:Int): Future[Int] = {
    val query = slickParticipants.filter(f=>f.id === id && f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }

  override def pureDeleteAll: Future[Int] = {
    val query = slickParticipants.filter(f=>f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


