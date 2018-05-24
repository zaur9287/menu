package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.RoleForm
import models.services.RoleService
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * created by Zaur.Narimanli
  * 22.05.2018
  * */


class RoleController @Inject() (
                               cc: ControllerComponents,
                               silhouette: Silhouette[DefaultEnv],
                               roleService: RoleService
                               ) extends AbstractController(cc) {

  def getAll = silhouette.SecuredAction.async { implicit request =>
    roleService.getAll.map(result => Ok(Json.toJson(result)))
  }

  def delete(roleID: Int) = silhouette.SecuredAction.async {implicit request =>
    roleService.delete(roleID)
      .map(result => Ok(Json.toJson(result)))
  }

  def findByID(roleID: Int) = silhouette.SecuredAction.async {implicit request =>
    roleService.findByID(roleID)
      .map(result => Ok(Json.toJson(result)))
  }

  def create = silhouette.SecuredAction.async(parse.json) {implicit request =>
    RoleForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"-> e.message))))) ,
      data => roleService.create(data)
        .map(result => Ok(Json.toJson(result))))
  }

  def update(roleID: Int) = silhouette.SecuredAction.async(parse.json) {implicit request =>
    RoleForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => roleService.update(roleID, data)
        .map(result => Ok(Json.toJson(result))))
  }
}