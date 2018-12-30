package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.{User, UserForm}
import models.services.{CompanyService, UserService}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/** created by Zaur.Narimanli
  * 22.12.2018
  */

class UserController @Inject()(
                              cc: ControllerComponents,
                              silhouette: Silhouette[DefaultEnv],
                              userService: UserService,
                              companyService: CompanyService
                              ) extends AbstractController(cc) {

  //  def getAll = silhouette.SecuredAction.async(parse.json) { implicit request =>
  //    UserFilterForm.form.bindFromRequest().fold(
  //      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
  //      data =>
  //        userService.getAll(data)
  //          .map(r => Ok(Json.toJson(r)))
  //    )
  //  }

  //  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
  //    UserForm.form.bindFromRequest().fold(
  //      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
  //      data =>
  //        userService.create(data)
  //          .map(result => Ok(Json.toJson(result)))
  //    )
  //  }

  //  def update(jobID: Int) = silhouette.SecuredAction.async(parse.json) { implicit request =>
  //    UserForm.form.bindFromRequest().fold(
  //      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
  //      data =>
  //        userService.update(jobID, data)
  //          .map(result => Ok(Json.toJson(result)))
  //    )
  //  }

  def delete(userID: UUID) = silhouette.SecuredAction.async { implicit request =>
    userService.delete(userID, request.identity.companyID).map(result => Ok(Json.toJson(result)))
  }


  def me = silhouette.SecuredAction.async { implicit request =>
    request.identity match {
      case user: User =>
        for {
          result <- companyService.findByID(user.companyID).map(m => m)
        } yield Ok(Json.toJson(user).as[JsObject] + ("company" -> Json.toJson(result)))

      case _ =>
        Future.successful(Ok(Json.toJson(request.identity)))

    }

  }
}