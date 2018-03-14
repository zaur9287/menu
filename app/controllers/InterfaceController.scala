package controllers

import javax.inject._

import models.caseClasses.{Interface, InterfaceForms}
import models.services.InterfaceService
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
class InterfaceController @Inject()(
                                cc: ControllerComponents,
                                interfaceService: InterfaceService
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


  //***********************************************************************interface operations
  def interfaceGet = Action.async { implicit request =>
    interfaceService.get.map(r =>
      Ok(Json.toJson(r))
    )
  }

  def interfaceCreate = Action.async(parse.json){ implicit request =>
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
  def interfaceDelete(id:Int) = Action.async { implicit request=>
    interfaceService.delete(id).map( r =>
      Ok(Json.toJson(r))
    )
  }
  def interfaceDeleteAll = Action.async { implicit request=>
    interfaceService.deleteAll.map( r =>
      Ok(Json.toJson(r))
    )
  }
  def interfaceGetInterface(id:Int) = Action.async{ implicit request=>
    val test = interfaceService.findInterfaceByID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def interfaceUpdate(id: Int) = Action.async(parse.json){ implicit request=>
    InterfaceForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        interfaceService.update(id, data).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }

  def interfacePureDelete(id:Int) = Action.async{ implicit request=>
    val test = interfaceService.pureDelete(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def interfacePureDeleteAll = Action.async{ implicit request=>
    val test = interfaceService.pureDeleteAll
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
}
