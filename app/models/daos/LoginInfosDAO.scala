package models.daos

import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import com.mohiva.play.silhouette.api.LoginInfo
import models.caseClasses.LInfo
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[LoginInfosDAOImpl])
trait LoginInfosDAO {
  def create(currRow: LInfo): Future[Option[LInfo]]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def findByUserID(id: String): Future[Option[LInfo]]
}

class LoginInfosDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends LoginInfosDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def create(currRow: LInfo): Future[Option[LInfo]] = {
    val mydb = DBLoginInfos(0,currRow.logininfo.providerID, currRow.logininfo.providerKey, currRow.userid)
    val query = slickLoginInfos.returning(slickLoginInfos) += mydb
    db.run(query).map ( r => Some(r.toLInfo) )
  }

  override def delete(id:Int): Future[Int] = {
    //val selectedRow = slickLoginInfos.filter(_.providerid==id.toString)
    val selectedRow = slickLoginInfos.filter(f =>f.providerid===id.toString)
    val deleteAction = selectedRow.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def deleteAll: Future[Int] = {
    val selectAll = slickLoginInfos
    val deletingAllAction = selectAll.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override def findByUserID(id:String): Future[Option[LInfo]] = {
    val query = slickLoginInfos.filter(f=>f.userid === id ).result
    db.run(query.headOption).map(_.map(_.toLInfo))
  }

}


