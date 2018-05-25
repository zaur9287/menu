package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.TableForm
import models.services.TableService
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
  * 21.05.2018
  */

class TableController @Inject() (
                                    cc: ControllerComponents,
                                    thisService: TableService,
                                    silhouette: Silhouette[DefaultEnv]
                                  ) extends AbstractController(cc){

  def getAll = silhouette.SecuredAction.async { implicit request =>
    thisService.getAll.map(r => Ok(Json.toJson(r)))
  }

  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
    TableForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        thisService.create(data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def update(tableID: Int) = silhouette.SecuredAction.async(parse.json) { implicit request =>
    TableForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        thisService.update(tableID, data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def delete(tableID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.delete(tableID).map(result => Ok(Json.toJson(result)))
  }

  def findByID(tableID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.findByID(tableID).map(result => Ok(Json.toJson(result)))
  }

  def getCompanyTables(companyID: Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.getCompanyTables(companyID)
      .map(result => Ok(Json.toJson(result)))
  }
}








