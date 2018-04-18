package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.{Contact, ContactForms}
import models.services.ContactService
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
class ContactController @Inject()(
                                cc: ControllerComponents,
                                contactService: ContactService,
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



  //***********************************************************************Client contact operations
  def ContactGet = silhouette.SecuredAction.async { implicit request =>
    contactService.get.map(r => Ok(Json.toJson(r)))
  }

  def ContactCreate = silhouette.SecuredAction.async(parse.json){ implicit request =>
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
  def ContactDelete(id:Int) = silhouette.SecuredAction.async { implicit request=>
    contactService.delete(id).map( r => Ok(Json.obj("result"->r)))
  }
  def ContactDeleteAll = silhouette.SecuredAction.async { implicit request=>
    contactService.deleteAll.map( r => Ok(Json.obj("result"->r)))
  }
  def ContactGetContact(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    contactService.findContactByID(id).map(j=>Ok(Json.toJson(j)))
  }
  def ContactGetByClientID(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    contactService.findContactByClientID(id).map(j=>Ok(Json.toJson(j)))
  }
  def ContactUpdate(id: Int) = silhouette.SecuredAction.async(parse.json){ implicit request=>
    ContactForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        contactService.update(id, data).map( r =>Ok(Json.obj("result"->r)))
      }
    )
  }

  def ContactPureDelete(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    contactService.pureDelete(id).map(j=>Ok(Json.obj("result"->j)))
  }
  def ContactPureDeleteAll = silhouette.SecuredAction.async{ implicit request=>
    contactService.pureDeleteAll.map(j=>Ok(Json.obj("result"->j)))
  }

}
