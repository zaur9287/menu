package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.services.ImageService
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv
import scala.concurrent.ExecutionContext.Implicits.global


class ImageController @Inject() (
                                cc: ControllerComponents,
                                silhouette: Silhouette[DefaultEnv],
                                imageService: ImageService
                                ) extends AbstractController(cc) {

  def getAll = silhouette.SecuredAction.async {implicit request =>
    imageService.getAll.map(result =>
    Ok(Json.toJson(result)))
  }

}