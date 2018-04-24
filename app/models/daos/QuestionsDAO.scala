package models.daos
import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Question
import models.caseClasses.Question._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[QuestionsDAOImpl])
trait QuestionsDAO {
  def get                                 : Future[Seq[Question]]
  def getByPage       (num:Int)           : Future[(Seq[Question],Int)]
  def create          (row: Question)     : Future[Option[Question]]
  def pureDelete      (id:Int)            : Future[Int]
  def pureDeleteAll                       : Future[Int]
  def delete          (selectedID:Int)    : Future[Int]
  def deleteAll                           : Future[Int]
  def update(id:Int,u: UpdateFormQuestion): Future[Int]
  def findByID        (id: Int)           : Future[Option[Question]]
  def findByQuizID    (id: Int)           : Future[Seq[Question]]
}

class QuestionsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends QuestionsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Question]] = {
    val query = slickQuestions.filter(r => r.deletedAt.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toQuestion).sortBy(_.id)
    )
  }

  override def getByPage(num: Int): Future[(Seq[Question], Int)] = {
    val q = slickQuestions.filter(r => r.deletedAt.isEmpty).drop(resultCount*num).take(resultCount).sortBy(_.id).result
    val t = slickQuestions.filter(r => r.deletedAt.isEmpty).length.result
    for {
      res<-db.run(q)
      all<-db.run(t)
    }yield (res.map(_.toQuestion),calculateMaxPageNum(all))
  }

  override def create(row: Question): Future[Option[Question]] = {
    val result = for{
      affected<-db.run(slickQuestions.filter(f=>f.deletedAt.isEmpty && f.text ===row.text && f.quizID ===row.quizID).result)
    }yield{
      if(affected.length==0){
        for{
          res<-db.run(slickQuestions.returning(slickQuestions) += DBQuestions(row.id, row.text,row.weight,row.quizID,row.createdAt, row.updatedAt, None))
        }yield{Some(res.toQuestion)}
      }else{Future(None)}
    }
    result.flatMap(r=>r)
//    val dBRow = DBQuestions(row.id, row.text,row.weight,row.quizID,row.createdAt, row.updatedAt, None)
//    val query = slickQuestions.returning(slickQuestions) += dBRow
//    db.run(query).map ( r => Some(r.toQuestion) )
  }

  override def delete(selectedID:Int): Future[Int] = {
    val selectedClient = slickQuestions.filter(_.id===selectedID)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def deleteAll: Future[Int] = {
    val selectAllClients = slickQuestions
    val deletingAllAction = selectAllClients.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override  def update(id:Int,updateForm: UpdateFormQuestion): Future[Int] = {
    val updateQuery = slickQuestions.filter(c => c.id === id && c.deletedAt.isEmpty)
      .map(c => (c.text,c.weight,c.quizID, c.updatedAt))
      .update((updateForm.text,updateForm.weight,updateForm.quizID, DateTime.now))
    db.run(updateQuery)
  }

  override def pureDelete(id:Int): Future[Int] = {
    val query = slickQuestions.filter(f=>f.id === id && f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }

  override def pureDeleteAll: Future[Int] = {
    val query = slickQuestions.filter(f=>f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }

  override def findByID(clientid:Int): Future[Option[Question]] = {
    val query = slickQuestions.filter(f=>f.id === clientid && f.deletedAt.isEmpty).result
    db.run(query.headOption).map(_.map(_.toQuestion))
  }

  override def findByQuizID(id: Int): Future[Seq[Question]] = {
    val query  = slickQuestions.filter(f=>f.deletedAt.isEmpty && f.quizID === id).result
    db.run(query).map(_.map(r=>r.toQuestion))
  }
}


