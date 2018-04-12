package models.daos
import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Answer
import models.caseClasses.Answer._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[AnswersDAOImpl])
trait AnswersDAO {
  def get: Future[Seq[Answer]]
  def create(row: Answer): Future[Option[Answer]]
  def pureDelete(id:Int): Future[Int]
  def pureDeleteAll: Future[Int]
  def delete(selectedID:Int): Future[Int]
  def deleteByQuestionID(q:Int): Future[Int]
  def pureDeleteByQuestionID(q:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(id:Int,updateForm: UpdateFormAnswer): Future[Option[Answer]]
  def findByID(id: Int): Future[Option[Answer]]
  def findByQuestionID(id: Int): Future[Seq[Answer]]
}

class AnswersDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends AnswersDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Answer]] = {
    val query = slickAnswers.filter(r => r.deletedAt.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toAnswer).sortBy(_.id)
    )
  }

  override def create(row: Answer): Future[Option[Answer]] = {
    val result  = for{
      affected <-db.run(slickAnswers.filter(f=>f.deletedAt.isEmpty && (f.text === row.text && f.questionID === row.questionID)).result)
    }yield{
      if (affected.length==0){
        for{
          res<-db.run(slickAnswers.returning(slickAnswers)+=DBAnswers(row.id, row.text,row.correct,row.questionID,row.createdAt, row.updatedAt, None))
        }yield{
          Some(res.toAnswer)
        }
      }else{Future(None)}
    }
    result.flatMap(r=>r)
//    val dBRow = DBAnswers(row.id, row.text,row.correct,row.questionID,row.createdAt, row.updatedAt, None)
//    val query = slickAnswers.returning(slickAnswers) += dBRow
//    db.run(query).map ( r => Some(r.toAnswer) )
  }

  override def delete(selectedID:Int): Future[Int] = {
    val selectedClient = slickAnswers.filter(_.id===selectedID)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def deleteAll: Future[Int] = {
    val selectAllClients = slickAnswers
    val deletingAllAction = selectAllClients.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override def deleteByQuestionID(q: Int): Future[Int] = {
    db.run(slickAnswers.filter(f=>f.deletedAt.isEmpty && f.questionID===q).delete)
  }

  override def pureDeleteByQuestionID(q: Int): Future[Int] = {
    val query = slickAnswers.filter(f=>f.deletedAt.isEmpty && f.questionID ===q)
      .map((_.deletedAt)).update(Some(DateTime.now()))
    db.run(query)
  }

  override  def update(id:Int,updateForm: UpdateFormAnswer): Future[Option[Answer]] = {
    val updateQuery = slickAnswers.filter(c => c.id === id && c.deletedAt.isEmpty)
      .map(c => (c.text,c.correct,c.questionID, c.updatedAt))
      .update((updateForm.text,updateForm.correct,updateForm.questionID, DateTime.now))

    val result = for {
      getUpdated <- findByID(id)
      updateRowCount <- if(getUpdated.isDefined){ db.run(updateQuery) } else { Future(0) }
    } yield {
      if(updateRowCount > 0){
        getUpdated.map(c => c.copy(id = id,text = updateForm.text, updatedAt = c.updatedAt,createdAt = c.createdAt,deletedAt = c.deletedAt))
      } else {None}
    }
    result
  }

  override def findByID(clientid:Int): Future[Option[Answer]] = {
    val query = slickAnswers.filter(f=>f.id === clientid && f.deletedAt.isEmpty).result
    db.run(query.headOption).map(_.map(_.toAnswer))
  }

  override def findByQuestionID(id: Int): Future[Seq[Answer]] = {
    val query = slickAnswers.filter(f=>f.deletedAt.isEmpty && f.questionID ===id).sortBy(_.id).result
    db.run(query).map(_.map(r=>r.toAnswer))
  }

  override def pureDelete(id:Int): Future[Int] = {
    val query = slickAnswers.filter(f=>f.id === id && f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }

  override def pureDeleteAll: Future[Int] = {
    val query = slickAnswers.filter(f=>f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


