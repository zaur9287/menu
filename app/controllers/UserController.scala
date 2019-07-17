package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.User
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

  def list = silhouette.SecuredAction.async(parse.json) { implicit request =>
    userService.list(request.identity.companyID).map(r => Ok(Json.toJson(r))
    )
  }

  def delete(userID: String) = silhouette.SecuredAction.async { implicit request =>
    userService.delete(UUID.fromString(userID), request.identity.companyID).map(result => Ok(Json.toJson(result)))
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