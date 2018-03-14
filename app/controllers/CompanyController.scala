package controllers

import javax.inject._

import models.caseClasses.{Company,CompanyForms}
import models.services.{CompanyService}
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class CompanyController @Inject()(
                                cc: ControllerComponents,
                                companyService:CompanyService
                              ) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }



  //***********************************************************************Company operations
  def CompanyGet = Action.async { implicit request =>
    companyService.get.map(r =>
      Ok(Json.toJson(r))
    )
  }

  def CompanyCreate = Action.async(parse.json){ implicit request =>
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
  def CompanyDelete(id:Int) = Action.async { implicit request=>
    companyService.delete(id).map( r =>
      Ok(Json.toJson(r))
    )
  }
  def CompanyDeleteAll = Action.async { implicit request=>
    companyService.deleteAll.map( r =>
      Ok(Json.toJson(r))
    )
  }
  def CompanyGetCompany(id:Int) = Action.async{ implicit request=>
    val test = companyService.findCompanyByID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def CompanyUpdate(id: Int) = Action.async(parse.json){ implicit request=>
    CompanyForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        companyService.update(id, data).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }

  def CompanyPureDelete(id:Int) = Action.async{ implicit request=>
    val test = companyService.pureDelete(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def CompanyPureDeleteAll = Action.async{ implicit request=>
    val test = companyService.pureDeleteAll
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }

}
