package models.daos
import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Category
import models.caseClasses.Category._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CategoriesDAOImpl])
trait CategoriesDAO {
  def get: Future[Seq[Category]]
  def getByPage       (num:Int)           : Future[(Seq[Category],Int)]
  def create          (row: Category)     : Future[Option[Category]]
  def pureDelete      (id:Int)            : Future[Int]
  def pureDeleteAll                       : Future[Int]
  def delete          (selectedID:Int)    : Future[Int]
  def deleteAll                           : Future[Int]
  def update          (id:Int,u: UpdateFormCategory): Future[Int]
  def findByID        (id: Int)           : Future[Option[Category]]
}

class CategoriesDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends CategoriesDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Category]] = {
    val query = slickCategories.filter(r => r.deletedAt.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toCategory).sortBy(_.id)
    )
  }

  override def getByPage(num: Int): Future[(Seq[Category], Int)] = {
    val q = slickCategories.filter(r => r.deletedAt.isEmpty).drop(resultCount*num).take(resultCount).sortBy(_.id).result
    val t = slickCategories.filter(r => r.deletedAt.isEmpty).length.result
    for {
      res<-db.run(q)
      all<-db.run(t)
    }yield (res.map(_.toCategory),calculateMaxPageNum(all))
  }

  override def create(row: Category): Future[Option[Category]] = {
    val result = for{
      exist <-db.run(slickCategories.filter(f=>f.deletedAt.isEmpty && f.name === row.name).result).map(r=>r)
    } yield{
      if (exist.length==0){
        for {
          row <- db.run(slickCategories.returning(slickCategories) += DBCategories(row.id, row.name, row.createdAt, row.updatedAt, None))
        }yield{Some(row.toCategory)}
      }
      else {Future(None)}
    }
    result.flatMap(r=>r)

//    val dBRow = DBCategories(row.id, row.name, row.createdAt, row.updatedAt, None)
//    val query = slickCategories.returning(slickCategories) += dBRow
//    db.run(query).map ( r => Some(r.toCategory) )
  }

  override def delete(selectedID:Int): Future[Int] = {
    val selectedClient = slickCategories.filter(_.id===selectedID)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def deleteAll: Future[Int] = {
    val selectAllClients = slickCategories
    val deletingAllAction = selectAllClients.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override  def update(id:Int,u: UpdateFormCategory): Future[Int] = {
    val updateQuery = slickCategories.filter(c => c.id === id && c.deletedAt.isEmpty)
      .map(c => (c.name, c.updatedAt))
      .update((u.name, DateTime.now))
    db.run(updateQuery)
  }

  override def findByID(clientid:Int): Future[Option[Category]] = {
    val query = slickCategories.filter(f=>f.id === clientid && f.deletedAt.isEmpty).result
    db.run(query.headOption).map(_.map(_.toCategory))
  }

  override def pureDelete(id:Int): Future[Int] = {
    val query = slickCategories.filter(f=>f.id === id && f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }

  override def pureDeleteAll: Future[Int] = {
    val query = slickCategories.filter(f=>f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


