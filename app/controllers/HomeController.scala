package controllers

import javax.inject._

import models.caseClasses.ClientForms.UpdateClientForm
import models.caseClasses.ContactForms.UpdateContactForm
import models.caseClasses.CompanyForms.UpdateCompanyForm
import models.caseClasses.CountryForms.UpdateCountryForm
import models.caseClasses._
import models.services._
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
class HomeController @Inject()(
                                cc: ControllerComponents,
                                clientService: ClientService,
                                contactService: ContactService,
                                companyService:CompanyService,
                                countryService:CountryService
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


  //***********************************************************************Client operations
  def ClientGet = Action.async { implicit request =>
    clientService.get.map(r =>
      Ok(Json.toJson(r))
    )
  }

  def ClientCreate = Action.async(parse.json){ implicit request =>
    Client.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        val client = Client(0, data._1, data._2, DateTime.parse(data._3), DateTime.now(), DateTime.now())
        clientService.create(client).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }
  def ClientDelete(id:Int) = Action.async { implicit request=>
    clientService.delete(id).map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ClientDeleteAll = Action.async { implicit request=>
    clientService.deleteAll.map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ClientGetClient(id:Int) = Action.async{ implicit request=>
    val test = clientService.findClientByID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ClientUpdate(id: Int) = Action.async(parse.json){ implicit request=>
    ClientForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        clientService.update(id, data).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }

  def ClientPureDelete(id:Int) = Action.async{ implicit request=>
    val test = clientService.pureDelete(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ClientPureDeleteAll = Action.async{ implicit request=>
    val test = clientService.pureDeleteAll
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }

  //***********************************************************************Client contact operations
  def ContactGet = Action.async { implicit request =>
    contactService.get.map(r =>
      Ok(Json.toJson(r))
    )
  }

  def ContactCreate = Action.async(parse.json){ implicit request =>
    Contact.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        val contact = Contact(0, data._1, data._2,data._3, DateTime.now(), DateTime.now())
        contactService.create(contact).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }
  def ContactDelete(id:Int) = Action.async { implicit request=>
    contactService.delete(id).map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ContactDeleteAll = Action.async { implicit request=>
    contactService.deleteAll.map( r =>
      Ok(Json.toJson(r))
    )
  }
  def ContactGetContact(id:Int) = Action.async{ implicit request=>
    val test = contactService.findContactByID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ContactGetByClientID(id:Int) = Action.async{ implicit request=>
    val test = contactService.findContactByClientID(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ContactUpdate(id: Int) = Action.async(parse.json){ implicit request=>
    ContactForms.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        contactService.update(id, data).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
      }
    )
  }

  def ContactPureDelete(id:Int) = Action.async{ implicit request=>
    val test = clientService.pureDelete(id)
    test.map(
      j=>Ok(Json.toJson(j))
    )
  }
  def ContactPureDeleteAll = Action.async{ implicit request=>
    val test = contactService.pureDeleteAll
    test.map(
      j=>Ok(Json.toJson(j))
    )
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
