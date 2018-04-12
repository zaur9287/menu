package models.daos
import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.{Answer, Question, Quiz}
import models.caseClasses.Quiz._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[QuizzesDAOImpl])
trait QuizzesDAO {
  def get: Future[Seq[Quiz]]
  def getQuestions(id:Int): Future[Option[(Question,Seq[Answer])]]
  def create(row: Quiz): Future[Option[Quiz]]
  def pureDelete(id:Int): Future[Int]
  def pureDeleteAll: Future[Int]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(id:Int,updateForm: UpdateFormQuiz): Future[Option[Quiz]]
  def findBySearchForm(tID:Int,cID:Int): Future[Seq[Quiz]]
  def findByID(id: Int): Future[Option[Quiz]]
  def findByCategoryID(id: Int): Future[Seq[Quiz]]
  def findByTrainingID(id: Int): Future[Seq[Quiz]]
}

class QuizzesDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends QuizzesDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Quiz]] = {
    val query = slickQuizzes.filter(r => r.deletedAt.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toQuiz).sortBy(_.id)
    )
  }
  override def getQuestions(id:Int): Future[Option[(Question,Seq[Answer])]] = {
    val q = slickQuestions.filter(f=>f.quizID === id && f.deletedAt.isEmpty)
      .joinLeft(slickAnswers.filter(_.deletedAt.isEmpty)).on(_.id === _.questionID).sortBy(_._2.map(r=>r.id))
    for {
      questionsAndAnswers<-db.run(q.result)
    }yield{
      val answers= questionsAndAnswers.map(_._2).flatten.map(r=>r.toAnswer)
      if (questionsAndAnswers.length>0){
        Some(questionsAndAnswers.head._1.toQuestion,answers)
      }else{
        None
      }
    }
  }

  override def create(row: Quiz): Future[Option[Quiz]] = {
    val result = for{
      affected<-db.run(slickQuizzes.filter(f=>f.deletedAt.isEmpty && f.name ===row.name && f.trainingID ===row.trainingID && f.categoryID ===row.categoryID).result)
    }yield{
      if(affected.length==0){
        for{
          res<-db.run(slickQuizzes.returning(slickQuizzes) += DBQuizzes(row.id, row.name,row.trainingID,row.categoryID, row.createdAt, row.updatedAt, None))
        }yield{Some(res.toQuiz)}
      }else{Future(None)}
    }
    result.flatMap(r=>r)
//    val dBRow = DBQuizzes(row.id, row.name,row.trainingID,row.categoryID, row.createdAt, row.updatedAt, None)
//    val query = slickQuizzes.returning(slickQuizzes) += dBRow
//    db.run(query).map ( r => Some(r.toQuiz) )
  }

  override def delete(selectedID:Int): Future[Int] = {
    val selected = slickQuizzes.filter(_.id===selectedID)
    val deleteAction = selected.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def deleteAll: Future[Int] = {
    val selectAll = slickQuizzes
    val deletingAllAction = selectAll.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override  def update(id:Int,updateForm: UpdateFormQuiz): Future[Option[Quiz]] = {
    val updateQuery = slickQuizzes.filter(c => c.id === id && c.deletedAt.isEmpty)
      .map(c => (c.name,c.trainingID,c.categoryID, c.updatedAt))
      .update((updateForm.name,updateForm.trainingID,updateForm.categoryID, DateTime.now))

    val result = for {
      getUpdated <- findByID(id)
      updateRowCount <- if(getUpdated.isDefined){ db.run(updateQuery) } else { Future(0) }
    } yield {
      if(updateRowCount > 0){
        getUpdated.map(c => c.copy(id = id,name = updateForm.name, updatedAt = c.updatedAt,createdAt = c.createdAt,deletedAt = c.deletedAt))
      } else {None}
    }
    result
  }

  override def findByID(id:Int): Future[Option[Quiz]] = {
    val query = slickQuizzes.filter(f=>f.id === id && f.deletedAt.isEmpty).result
    db.run(query.headOption).map(_.map(_.toQuiz))
  }

  override def findByCategoryID(id: Int): Future[Seq[Quiz]] = {
    val query = slickQuizzes.filter(f=>f.deletedAt.isEmpty && f.categoryID ===id).result
    db.run(query.map(_.map(r=>r.toQuiz)))
  }

  override def findByTrainingID(id: Int): Future[Seq[Quiz]] = {
    val query = slickQuizzes.filter(f=>f.deletedAt.isEmpty && f.trainingID ===id).result
    db.run(query).map(_.map(r=>r.toQuiz))
  }

  override def pureDelete(id:Int): Future[Int] = {
    val query = slickQuizzes.filter(f=>f.id === id && f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }

  override def pureDeleteAll: Future[Int] = {
    val query = slickQuizzes.filter(f=>f.deletedAt.isEmpty).map(c=>(c.deletedAt)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
  override def findBySearchForm(tID:Int,cID:Int): Future[Seq[Quiz]] = {
    val query = slickQuizzes.filter(f=>f.deletedAt.isEmpty && f.trainingID ===tID && f.categoryID ===cID).result
    db.run(query).map(_.map(r=>r.toQuiz))
  }

}


