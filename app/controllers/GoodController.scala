package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.GoodForm
import models.services.GoodService
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * created by Zaur.Narimanli
  * 22.05.2018
  * */

class GoodController @Inject() (
                               cc: ControllerComponents,
                               silhouette: Silhouette[DefaultEnv],
                               goodService: GoodService
                               ) extends AbstractController(cc) {

  def getAll = silhouette.SecuredAction.async { implicit request =>
    goodService.getAll.map(r => Ok(Json.toJson(r)))
  }

  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
    GoodForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        goodService.create(data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def update(goodID: Int) = silhouette.SecuredAction.async(parse.json) { implicit request =>
    GoodForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        goodService.update(goodID, data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def delete(goodID: Int) = silhouette.SecuredAction.async { implicit request =>
    goodService.delete(goodID).map(result => Ok(Json.toJson(result)))
  }

  def findByID(goodID: Int) = silhouette.SecuredAction.async { implicit request =>
    goodService.findByID(goodID).map(result => Ok(Json.toJson(result)))
  }


}