package models.daos
import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Training
import models.caseClasses.Training._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TrainingsDAOImpl])
trait TrainingsDAO {
  def get                                 : Future[Seq[Training]]
  def getByPage       (num:Int)           : Future[(Seq[Training],Int)]
  def create          (row: Training)     : Future[Option[Training]]
  def pureDelete      (id:Int)            : Future[Int]
  def pureDeleteAll                       : Future[Int]
  def delete          (selectedID:Int)    : Future[Int]
  def deleteAll                           : Future[Int]
  def update          (id:Int,u: UpdateFormTraining): Future[Int]
  def findByID        (id: Int)           : Future[Option[Training]]
}

class TrainingsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends TrainingsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Training]] = {
    val query = slickTrainings.filter(r => r.deletedAt.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toTraining).sortBy(_.id)
    )
  }

  override def getByPage(num: Int):Future[(Seq[Training],Int)] = {
    val q = slickTrainings.filter(r => r.deletedAt.isEmpty).drop(resultCount*num).take(resultCount).sortBy(_.id).result
    val t = slickTrainings.filter(r => r.deletedAt.isEmpty).length.result
    for {
      res<-db.run(q)
      all<-db.run(t)
    }yield (res.map(_.toTraining),calculateMaxPageNum(all))
  }


  override def create(row: Training): Future[Option[Training]] = {
    val result = for{
      affected<-db.run(slickTrainings.filter(f=>f.deletedAt.isEmpty && f.name ===row.name).result)
    }yield{
      if(affected.length==0){
        for{
          res<-db.run(slickTrainings.returning(slickTrainings) += DBTraining(row.id, row.name, row.createdAt, row.updatedAt, None))
        }yield{Some(res.toTraining)}
      }else{Future(None)}
    }
    result.flatMap(r=>r)
//    val dBRow = DBTraining(row.id, row.name, row.createdAt, row.updatedAt, None)
//    val query = slickTrainings.returning(slickTrainings) += dBRow
//    db.run(query).map ( r => Some(r.toTraining) )
  }

  override def delete(selectedID:Int): Future[Int] = {
    val selectedClient = slickTrainings.filter(_.id===selectedID)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def deleteAll: Future[Int] = {
    val selectAllClients = slickTrainings
    val deletingAllAction = selectAllClients.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override  def update(id:Int,u: UpdateFormTraining): Future[Int] = {
    val updateQuery = slickTrainings.filter(c => c.id === id && c.deletedAt.isEmpty)
      .map(c => (c.name, c.updatedAt))
      .update((u.name, DateTime.now))
    db.run(updateQuery)
  }

  override def findByID(clientid:Int): Future[Option[Training]] = {
    val query = slickTrainings.filter(f=>f.id === clientid && f.deletedAt.isEmpty).result
    db.run(query.headOption).map(_.map(_.toTraining))
  }

  override def pureDelete(id:Int): Future[Int] = {
    val query = slickTrainings.filter(f=>f.id === id && f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }

  override def pureDeleteAll: Future[Int] = {
    val query = slickTrainings.filter(f=>f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


