package models.daos
import org.joda.time.DateTime
import java.util.Formatter.DateTime

import akka.actor.Status.Success
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Client
import models.caseClasses.ClientForms.UpdateClientForm
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ClientsDAOImpl])
trait ClientsDAO {
  def get: Future[Seq[Client]]
  def create(client: Client): Future[Option[Client]]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(updateClientForm: UpdateClientForm): Future[Option[Client]]
  def findClientByID(clientid: Int): Future[Option[Client]]
}

class ClientsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends ClientsDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Client]] = {
    val query = slickClients.filter(r => r.deleted_at.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toClients).sortBy(_.id).reverse
    )
  }

  override def create(client: Client): Future[Option[Client]] = {
    val dBClient = DBClient(client.id, client.name, client.description, client.expireDate, client.createdAt, client.updatedAt, None)
    val query = slickClients.returning(slickClients) += dBClient
    db.run(query).map ( r => Some(r.toClients) )
  }

  override def delete(selectedID:Int): Future[Int] = {
    val selectedClient = slickClients.filter(_.id===selectedID)
    val deleteAction = selectedClient.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }

  override def deleteAll: Future[Int] = {
    val selectAllClients = slickClients
    val deletingAllAction = selectAllClients.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }
  override  def update(updateClientForm: UpdateClientForm): Future[Option[Client]] = {
    //deyerlerin hamisi gelerse
    //    val foundedClient = findClientByID(client.id)
    //    val dBClient = DBClient(client.id, client.name, client.description, client.expireDate, client.createdAt, DateTime.now, None)
    //    val query= slickClients.filter(_.id === client.id).update(dBClient)
    //    db.run(query).map(r=> Some(client))
    val updateQuery = slickClients.filter(c => c.id === client.id && c.deleted_at.isEmpty)
      .map(c =>
        (c.name, c.desc, c.column(expire_date), c.updated_at)
      )
      .update((client.name, client.description, client.expireDate, client.updatedAt))

    val result = for {
      getUpdatedClient <- findClientByID(client.id)
      updateRowCount <- if(getUpdatedClient.isDefined){ db.run(updateQuery) } else { Future(0) }
    } yield {
      if(updateRowCount > 0){
        getUpdatedClient.map(c => c.copy(name = client.name, description = client.description, expireDate = client.expireDate, updatedAt = client.updatedAt))
      } else {
        None
      }
    }

    result



    //    val query = for {
    //      p <- slickClients
    //      if p.id === client.id
    //    } yield (p.name, p.desc,p.expire_date,p.updated_at)
    //    val updateAction = query.update(client.name, client.description, client.expireDate,client.updatedAt)
    //    val test  = db.run(updateAction).map(r=>r)
    //    test.flatMap {
    //      case Success(res) => DBIO.successful(res)
    //      case Failure(t)   => DBIO.failed(t)
    //    }
    //    findClientByID(client.id)


    //    //******************************************************************************
    //    //amator sorgu/ baza ile 4defe is gorulur.
    //    val qName = for { c <- slickClients if c.id === id} yield c.name
    //    val updateName = qName.update(clientMap("name"))
    //    val testName = db.run(updateName).map(r=>r)
    //
    //    val qDescription = for { c <- slickClients if c.id === id} yield c.desc
    //    val updateDesc = qDescription.update(Some(clientMap("description").toString))
    //    val testDesc = db.run(updateDesc).map(r=>r)
    //
    //    val qUpdatedAt = for { c <- slickClients if c.id === id} yield c.updated_at
    //    val updateUpdatedAt = qUpdatedAt.update(DateTime.now)
    //    val testuUpdatedAt = db.run(updateUpdatedAt).map(r=>r)
    //
    //    findClientByID(id)

    //db row duzelt hansilarki deyer ile gelib....
    //daha sonra hemin deyerleri birbasa update ucun gonder...
    //    val clientID = clientMap("id")
    //    val id:Int = if (clientID==null || clientID=="") 0 else clientID.asInstanceOf[Integer].toInt
    //    //ad teyin olunubsa
    //    val clientName:Boolean = clientMap("name").asInstanceOf[Option[String]].isDefined
    //    var name = if (clientName == true) clientMap("name").isInstanceOf[Option].getOrElse("deyer alinmadi")
    //    var nametest = if (clientName == true) clientMap("name").getOrElse("deyer alinmadi")
    //    //name = if (name != null) name.asInstanceOf[Some].get
    //
    //    //description teyin olunubsa
    //    val clientDesc:Boolean = clientMap("description").asInstanceOf[Option[String]].isDefined
    //    var desc = if (clientDesc == true) clientMap("description")
    //    //expire date teyin olunubsa
    //    val clientExpDate:Boolean = clientMap("description").asInstanceOf[Option[String]].isDefined
    //    val expireDate = if (clientExpDate == true) clientMap("expireDate")


    //    val clientDesc = clientMap("description")
    //    val desc = if (clientDesc==null || clientDesc=="" || clientName.asInstanceOf[Option].isDefined==false) 0 else desc
    //    val ExpireDate = clientMap("expireDate")
    //    val ExpireDate = if (ExpireDate==null || ExpireDate=="" || ExpireDate.asInstanceOf[Option].isDefined==false) 0 else ExpireDate


    //    val query = for {
    //      m <- slickClients
    //      u <- clientMap if m.id === id
    //    } yield {
    //      (m.id, m.name, m.desc)
    //    }

    //    val q = for {
    //      c <- slickClients
    //      if c.id === id
    //    } yield {
    //
    //      clientMap foreach{
    //        case (key,keyvalue) =>keyvalue match {
    //          case keyvalue:String=>c.column(key)
    //          case keyvalue:Int=>c.column(key)
    //          case _=>c.column(key)
    //        }
    //      }
    //    }
    //    val updateAction = q.update(clientMap)
    //    val test = db.run(updateAction).map(r=>r)


    //    clientMap foreach{
    //      case (key,value)=>
    //        key match {
    //          val q = for {
    //        c <- slickClients if c.id === id
    //        } yield c.name
    //          val updateAction = q.update(clientMap(key))
    //          val test = db.run(updateAction).map(r=>r)
    //
    //
    ////          case key=>clientMap(key)
    ////          case "name"=>clientMap(key)
    ////          case "desc"=>clientMap(key)
    //        }
    //    }


    //    val q = for { c <- slickClients if c.id === id} yield c.name
    //    val updateAction = q.update(clientMap("name"))
    //    val test = db.run(updateAction).map(r=>r)


    //eger ad doludursa
    //    val id:Int =0
    //    if (clientMap("id").isNonEmpty){
    //      id = clientMap("id").value
    //    }
    //    val name:String =""
    //    if (clientMap("name").isEmpty){
    //      name = clientMap("name").value
    //    }
    //    val desc:String =""
    //    if (clientMap("description").isEmpty){
    //      desc = clientMap("description").value
    //    }
    //    val expireDate:String =""
    //    if (clientMap("expireDate").isEmpty){
    //      expireDate = clientMap("expireDate").value
    //    }


    //clientMap("name").value
    //    clientMap match {
    //
    //
    //    }
    //    val clientID = clientMap("id")
    //    val id:Int = if (clientID==null || clientID=="") 0 else clientID
    //
    //    val q = for { c <- slickClients if c.id === id} yield c.name
    //    val updateAction = q.update(clientMap("name"))
    //    val test = db.run(updateAction).map(r=>r)
    //    findClientByID(2)

    //    val update = slickClients.filter(_.id == clientID).map(_.name).
    //      update(slickClients.map(_.name), ("Jane"))

    //    val update = slickClients.filter(_.id == id).map(_.name
    ////      clientMap foreach{
    ////        case(key,value)=>key match {
    ////          case "name" =>s.name
    ////          case "description" =>s.desc
    ////          case "expireDate" =>s.expire_date
    ////        }
    ////      }
    //    ).updateReturning(
    //      slickClients.map(_.name
    //        //s=>
    ////      clientMap foreach{
    ////        case(key,value)=>key match {
    ////          case "name" =>s.name
    ////          case "description" =>s.desc
    ////          case "expireDate" =>s.expire_date
    ////        }
    ////      }
    //        ), (clientMap("name"))
    //    )




    //    val currentClient =  for {
    //          currentClient <- slickClients
    //          if (currentClient.id == id)
    //          clientMap foreach{
    //            case(key,value)=>currentClient.column(key)->value
    //          }
    //        } yield currentClient
    //
    //    db.run(currentClient.update())



    //    slickClients.filter(_.id === clientID)(r => {
    //      val metadata = r.rs.getMetaData
    //      (1 to r.numColumns).flatMap(i => {
    //        val columnName = metadata.getColumnName(i).toLowerCase
    //        val columnValue = r.nextObjectOption
    //        columnValue.map(columnName -> _)
    //      }).toMap
    //    })


    //val query= slickClients.filter(_.id === clientID)
    //select * from clients where id = $clientID
    //UPDATE clients SET column1 = value1, column2 = value2 WHERE id = $clientID;
    //    for {
    //      currentClient <- slickClients
    //      if (currentClient == id)
    //      clientMap foreach{
    //        case(column,value)=>currentClient.columns(column)=value
    //      }
    //    } yield currentClient

    //    val res = (1 to pr.numColumns).map{ i=> md.getColumnName(i) -> clientMap.getObject(i) }.toMap
    //    pr.nextRow // <- use Slick's advance method to avoid endless loop
    //    res
    //  }

    //???

    //    val query= slickClients.filter(_.id === clientID)
    //    val query = for { c <- slickClients if c.id === clientID } yield c
    //    val updateAction = query.update()

    //    val query = slickClients.filter(f=>f.id === id).result
    //    db.run(query.headOption).map(_.map(_.toClients))

    //    val dBClient = DBClient(client.id, client.name, client.description, client.expireDate, client.createdAt, client.updatedAt, None)
    //    val query= slickClients.filter(_.id === client.id).update(dBClient)
    //    db.run(query).map(r=>client)
    //???
  }
  override def findClientByID(clientid:Int): Future[Option[Client]] = {
    val query = slickClients.filter(f=>f.id === clientid).result
    db.run(query.headOption).map(_.map(_.toClients))
  }
}


