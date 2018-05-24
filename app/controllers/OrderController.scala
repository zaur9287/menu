package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.OrderForm
import models.services.OrderService
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/** created by Zaur.Narimanli
  * 22.05.2018
  */


class OrderController @Inject() (
                                cc : ControllerComponents,
                                silhouette: Silhouette[DefaultEnv],
                                orderService: OrderService
                                ) extends AbstractController(cc) {

  def getAll = silhouette.SecuredAction.async { implicit request =>
    orderService.getAll.map(r => Ok(Json.toJson(r)))
  }

  def create = silhouette.SecuredAction.async(parse.json) { implicit request =>
    OrderForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        orderService.create(data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def update(orderID: Int) = silhouette.SecuredAction.async(parse.json) { implicit request =>
    OrderForm.form.bindFromRequest().fold(
      hasErrors => Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key"->e.key, "message"->e.message))))),
      data =>
        orderService.update(orderID, data)
          .map(result => Ok(Json.toJson(result)))
    )
  }

  def delete(orderID: Int) = silhouette.SecuredAction.async { implicit request =>
    orderService.delete(orderID).map(result => Ok(Json.toJson(result)))
  }

  def findByID(orderID: Int) = silhouette.SecuredAction.async { implicit request =>
    orderService.findByID(orderID).map(result => Ok(Json.toJson(result)))
  }

}