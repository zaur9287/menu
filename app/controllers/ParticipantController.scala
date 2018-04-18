package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.Participant
import models.caseClasses.Participant._
import models.services.ParticipantService
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ParticipantController @Inject()(
                                    cc: ControllerComponents,
                                    thisService: ParticipantService,
                                    silhouette: Silhouette[DefaultEnv]
                                  ) extends AbstractController(cc) {

  protected def normalizePhoneNumber(number:String):String ={
    var tempNumber = number
    if (number.take(1)=="+")                            tempNumber = number.replace(number.take(1),"00")
    if (number.take(2) == "05" || number.take(2)=="07") tempNumber = "00994"+number.slice(1,number.length)
    tempNumber
  }

  def Get = silhouette.SecuredAction.async { implicit request =>
    thisService.get.map(r => Ok(Json.toJson(r)))
  }

  def Create = silhouette.SecuredAction.async(parse.json){ implicit request =>
    Participant.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        thisService.create(Participant(0, data.name,normalizePhoneNumber(data.phone),data.company,data.categoryID, DateTime.now(), DateTime.now(),None))
          .map( r =>
            if(r.isDefined){
              Ok(Json.toJson(r.get))
            } else {
              BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
            }
          )
      }
    )
  }
  def Delete(id:Int) = silhouette.SecuredAction.async { implicit request=>
    thisService.delete(id).map( r => Ok(Json.obj("result"->r)))
  }
  def DeleteAll = silhouette.SecuredAction.async { implicit request=>
    thisService.deleteAll.map( r => Ok(Json.obj("result"->r)))
  }
  def GetOne(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    thisService.findByID(id).map(j=>Ok(Json.toJson(j)))
  }

  def getByCategoryID(id:Int) = silhouette.SecuredAction.async {implicit request =>
    val test = thisService.findByCategoryID(id)
    test.map(r=>Ok(Json.toJson(r)))
  }
  def Update(id: Int) = silhouette.SecuredAction.async(parse.json){ implicit request=>
    Participant.updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        var testData = Participant.UpdateFormParticipant(data.name,normalizePhoneNumber(data.phone),data.company,data.categoryID)
        thisService.update(id, testData).map( r => Ok(Json.obj("result"->r)))
      }
    )
  }

  def PureDelete(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    thisService.pureDelete(id).map(j=>Ok(Json.obj("result"->j)))
  }
  def PureDeleteAll = silhouette.SecuredAction.async{ implicit request=>
    thisService.pureDeleteAll.map(j=>Ok(Json.obj("result"->j)))
  }
}
