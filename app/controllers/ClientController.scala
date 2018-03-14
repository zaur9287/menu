package controllers

import javax.inject._

import models.caseClasses.{Client,ClientForms}
import models.services.ClientService
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class ClientController @Inject()(
                                cc: ControllerComponents,
                                clientService: ClientService
                              ) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }


  //***********************************************************************Client operations
  def ClientGet = Action.async { implicit request =>
    clientService.get.map(r =>
      Ok(Json.toJson(r))
    )
  }

  def ClientCreate = Action.async(parse.json){ implicit request =>
    Client.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        val client = Client(0, data._1, data._2, DateTime.parse(data._3), DateTime.now(), DateTime.now())
        clientService.create(client).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }
  def ClientDelete(id:Int) = Action.async { implicit request=>
    clientService.delete(id).map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ClientDeleteAll = Action.async { implicit request=>
    clientService.deleteAll.map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ClientGetClient(id:Int) = Action.async{ implicit request=>
    val test = clientService.findClientByID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ClientUpdate(id: Int) = Action.async(parse.json){ implicit request=>
    ClientForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        clientService.update(id, data).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }

  def ClientPureDelete(id:Int) = Action.async{ implicit request=>
    val test = clientService.pureDelete(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ClientPureDeleteAll = Action.async{ implicit request=>
    val test = clientService.pureDeleteAll
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
}
