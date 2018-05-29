package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.CompanyForm
import models.services.CompanyService
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * created by Zaur.Narimanli
  * 19.05.2018
  */

class CompanyController @Inject() (
                                  cc: ControllerComponents,
                                  thisService: CompanyService,
                                  silhouette: Silhouette[DefaultEnv]
                                  ) extends AbstractController(cc){

  def getAll = silhouette.SecuredAction.async { implicit request =>
    thisService.getAll.map(r => Ok(Json.toJson(r)))
  }

  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
    CompanyForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        thisService.create(data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def update(companyID: Int) = silhouette.SecuredAction.async(parse.json) { implicit request =>
    CompanyForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        thisService.update(companyID, data)
        .map(result => Ok(Json.toJson(result)))
    )
  }

  def delete(companyID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.delete(companyID)
      .map(result => Ok(Json.toJson(result)))
  }

  def findByID(companyID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.findByID(companyID)
      .map(result => Ok(Json.toJson(result)))
  }

  def getCompanyUsers(companyID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.getCompanyUsers(companyID)
      .map(result => Ok(Json.toJson(result)))
  }

  def getCompanyContacts(companyID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.getCompanyContacts(companyID)
      .map(result => Ok(Json.toJson(result)))
  }

}








