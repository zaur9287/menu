package controllers

import javax.inject.Inject
import javax.management.BadStringOperationException

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.ContactForm
import models.services.ContactService
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * created by Zaur.Narimanli
  * 21.05.2018
  * */

class ContactController @Inject() (
                                  cc:ControllerComponents,
                                  thisService: ContactService,
                                  silhouette: Silhouette[DefaultEnv]
                                  ) extends AbstractController(cc) {

  def getAll = silhouette.SecuredAction.async { implicit request =>
    thisService.getAll.map(r => Ok(Json.toJson(r)))
  }

  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
    ContactForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        thisService.create(data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def update(contactID: Int, companyID: Int) = silhouette.SecuredAction.async(parse.json) { implicit request =>
    var userID: Option[String] = None
    try{
      userID = Some(request.getQueryString("userID")).getOrElse(None)
    }catch{
      case ex: BadStringOperationException => userID = None
    }

    ContactForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        thisService.update(contactID, companyID, userID, data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def delete(contactID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.delete(contactID).map(result => Ok(Json.toJson(result)))
  }

  def findByID(contactID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.findByID(contactID).map(result => Ok(Json.toJson(result)))
  }

}