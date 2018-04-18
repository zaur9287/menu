package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.{Company, CompanyForms}
import models.services.CompanyService
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class CompanyController @Inject()(
                                cc: ControllerComponents,
                                companyService:CompanyService,
                                silhouette: Silhouette[DefaultEnv]
                              ) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */

  //***********************************************************************Company operations
  def CompanyGet = silhouette.SecuredAction.async { implicit request =>
    companyService.get.map(r => Ok(Json.toJson(r)))
  }

  def CompanyCreate = silhouette.SecuredAction.async(parse.json){ implicit request =>
    Company.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        val company = Company(0, data._1, data._2, DateTime.now(), DateTime.now())
        companyService.create(company).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }
  def CompanyDelete(id:Int) = silhouette.SecuredAction.async { implicit request=>
    companyService.delete(id).map( r => Ok(Json.obj("result"->r)))
  }
  def CompanyDeleteAll = silhouette.SecuredAction.async { implicit request=>
    companyService.deleteAll.map( r => Ok(Json.obj("result"->r)))
  }
  def CompanyGetCompany(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    companyService.findCompanyByID(id).map(j=>Ok(Json.toJson(j)))
  }
  def CompanyUpdate(id: Int) = silhouette.SecuredAction.async(parse.json){ implicit request=>
    CompanyForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        companyService.update(id, data).map( r =>Ok(Json.toJson(r)))
      }
    )
  }

  def CompanyPureDelete(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    companyService.pureDelete(id).map(j=>Ok(Json.obj("result"->j)))
  }
  def CompanyPureDeleteAll = silhouette.SecuredAction.async{ implicit request=>
    companyService.pureDeleteAll.map(j=>Ok(Json.obj("result"->j)))
  }

}
