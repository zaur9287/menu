package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.{Interface, InterfaceForms}
import models.services.InterfaceService
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
class InterfaceController @Inject()(
                                cc: ControllerComponents,
                                interfaceService: InterfaceService,
                                silhouette: Silhouette[DefaultEnv]
                              ) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */

  //***********************************************************************interface operations
  def interfaceGet = silhouette.SecuredAction.async { implicit request =>
    interfaceService.get.map(r => Ok(Json.toJson(r)))
  }

  def interfaceCreate = silhouette.SecuredAction.async(parse.json){ implicit request =>
    Interface.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        val interface = Interface(0, data._1, data._2, DateTime.now(), DateTime.now())
        interfaceService.create(interface).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }
  def interfaceDelete(id:Int) = silhouette.SecuredAction.async { implicit request=>
    interfaceService.delete(id).map( r => Ok(Json.obj("result"->r)))
  }
  def interfaceDeleteAll = silhouette.SecuredAction.async { implicit request=>
    interfaceService.deleteAll.map( r => Ok(Json.obj("result"->r)))
  }
  def interfaceGetInterface(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    interfaceService.findInterfaceByID(id).map(j=>Ok(Json.toJson(j)))
  }
  def interfaceUpdate(id: Int) = silhouette.SecuredAction.async(parse.json){ implicit request=>
    InterfaceForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        interfaceService.update(id, data).map( r =>Ok(Json.toJson(r)))
      }
    )
  }

  def interfacePureDelete(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    interfaceService.pureDelete(id).map(j=>Ok(Json.obj("result"->j)))
  }
  def interfacePureDeleteAll = silhouette.SecuredAction.async{ implicit request=>
    interfaceService.pureDeleteAll.map(j=>Ok(Json.obj("result"->j)))
  }
}
