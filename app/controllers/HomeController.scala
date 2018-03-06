package controllers

import javax.inject._
import models.caseClasses.Client
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
class HomeController @Inject()(
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

  def get = Action.async { implicit request =>
    clientService.get.map(r =>
      Ok(Json.toJson(r))
    )
  }

  def create = Action.async(parse.json){ implicit request =>
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

}
