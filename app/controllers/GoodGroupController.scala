package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.GoodGroupForm
import models.services.GoodGroupService
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * created by Zaur.Narimanli
  * 22.05.2018
  * */

class GoodGroupController @Inject()(
                               cc: ControllerComponents,
                               silhouette: Silhouette[DefaultEnv],
                               GoodGroupService: GoodGroupService
                               ) extends AbstractController(cc) {

  def getAll = silhouette.SecuredAction.async { implicit request =>
    GoodGroupService.getAll.map(r => Ok(Json.toJson(r)))
  }

  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
    GoodGroupForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        GoodGroupService.create(data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def update(goodGroupID: Int) = silhouette.SecuredAction.async(parse.json) { implicit request =>
    GoodGroupForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        GoodGroupService.update(goodGroupID, data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def delete(goodGroupID: Int) = silhouette.SecuredAction.async { implicit request =>
    GoodGroupService.delete(goodGroupID).map(result => Ok(Json.toJson(result)))
  }

  def findByID(goodGroupID: Int) = silhouette.SecuredAction.async { implicit request =>
    GoodGroupService.findByID(goodGroupID).map(result => Ok(Json.toJson(result)))
  }


}