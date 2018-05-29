package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.{JobFilterForm, JobForm}
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv
import models.services.JobService
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/** creted by Zaur.Narimanli
  * 22.05.2018
  */


class JobController @Inject() (
                              cc: ControllerComponents,
                              silhouette: Silhouette[DefaultEnv],
                              jobService: JobService
                              ) extends AbstractController(cc) {

  def getAll = silhouette.SecuredAction.async(parse.json) { implicit request =>
    JobFilterForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        jobService.getAll(data)
          .map(r => Ok(Json.toJson(r)))
    )
  }

  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
    JobForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        jobService.create(data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def update(jobID: Int) = silhouette.SecuredAction.async(parse.json) { implicit request =>
    JobForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        jobService.update(jobID, data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def delete(jobID: Int) = silhouette.SecuredAction.async { implicit request =>
    jobService.delete(jobID).map(result => Ok(Json.toJson(result)))
  }

  def findByID(jobID: Int) = silhouette.SecuredAction.async { implicit request =>
    jobService.findByID(jobID).map(result => Ok(Json.toJson(result)))
  }

}