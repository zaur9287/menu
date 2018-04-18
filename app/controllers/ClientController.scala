package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.{Client, ClientForms}
import models.services.ClientService
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class ClientController @Inject()(
                                cc: ControllerComponents,
                                clientService: ClientService,
                                silhouette: Silhouette[DefaultEnv]
                              ) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = silhouette.SecuredAction { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }


  //***********************************************************************Client operations
  def ClientGet = silhouette.SecuredAction.async { implicit request =>
    clientService.get.map(r => Ok(Json.toJson(r)))
  }

  def ClientCreate = silhouette.SecuredAction.async(parse.json){ implicit request =>
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
  def ClientDelete(id:Int) = silhouette.SecuredAction.async { implicit request=>
    clientService.delete(id).map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ClientDeleteAll = silhouette.SecuredAction.async { implicit request=>
    clientService.deleteAll.map( r => Ok(Json.obj("result"->r)))
  }
  def ClientGetClient(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    clientService.findClientByID(id).map(j=>Ok(Json.toJson(j)))
  }
  def ClientUpdate(id: Int) = silhouette.SecuredAction.async(parse.json){ implicit request=>
    ClientForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        clientService.update(id, data).map( r =>Ok(Json.obj("result"->r)))
      }
    )
  }

  def ClientPureDelete(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    clientService.pureDelete(id).map(j=>Ok(Json.obj("result"->j)))
  }
  def ClientPureDeleteAll = silhouette.SecuredAction.async{ implicit request=>
    clientService.pureDeleteAll.map(j=>Ok(Json.obj("result"->j)))
  }
}
