package controllers

import javax.inject._

import models.caseClasses.{Contact,ContactForms}
import models.services.{ContactService}
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
class ContactController @Inject()(
                                cc: ControllerComponents,
                                contactService: ContactService
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



  //***********************************************************************Client contact operations
  def ContactGet = Action.async { implicit request =>
    contactService.get.map(r =>
      Ok(Json.toJson(r))
    )
  }

  def ContactCreate = Action.async(parse.json){ implicit request =>
    Contact.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        val contact = Contact(0, data._1, data._2,data._3, DateTime.now(), DateTime.now())
        contactService.create(contact).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }
  def ContactDelete(id:Int) = Action.async { implicit request=>
    contactService.delete(id).map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ContactDeleteAll = Action.async { implicit request=>
    contactService.deleteAll.map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ContactGetContact(id:Int) = Action.async{ implicit request=>
    val test = contactService.findContactByID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ContactGetByClientID(id:Int) = Action.async{ implicit request=>
    val test = contactService.findContactByClientID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ContactUpdate(id: Int) = Action.async(parse.json){ implicit request=>
    ContactForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        contactService.update(id, data).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }

  def ContactPureDelete(id:Int) = Action.async{ implicit request=>
    val test = contactService.pureDelete(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ContactPureDeleteAll = Action.async{ implicit request=>
    val test = contactService.pureDeleteAll
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }

}
