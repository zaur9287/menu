package controllers

import javax.inject._

import models.caseClasses.{Country,CountryForms}
import models.services.CountryService
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
class CountryController @Inject()(
                                cc: ControllerComponents,
                                countryService:CountryService
                              ) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */

  //***********************************************************************Country operations
  def CountryGet = Action.async { implicit request =>
    countryService.get.map(r =>
      Ok(Json.toJson(r))
    )
  }

  def CountryCreate = Action.async(parse.json){ implicit request =>
    Country.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        val country = Country(0, data._1, data._2, DateTime.now(), DateTime.now())
        countryService.create(country ).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }
  def CountryDelete(id:Int) = Action.async { implicit request=>
    countryService.delete(id).map( r =>
      Ok(Json.toJson(r))
    )
  }
  def CountryDeleteAll = Action.async { implicit request=>
    countryService.deleteAll.map( r =>
      Ok(Json.toJson(r))
    )
  }
  def CountryGetCountry(id:Int) = Action.async{ implicit request=>
    val test = countryService.findCountryByID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def CountryUpdate(id: Int) = Action.async(parse.json){ implicit request=>
    CountryForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        countryService.update(id, data).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }

  def CountryPureDelete(id:Int) = Action.async{ implicit request=>
    val test = countryService.pureDelete(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def CountryPureDeleteAll = Action.async{ implicit request=>
    val test = countryService.pureDeleteAll
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }


}
