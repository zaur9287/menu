package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.Training
import models.caseClasses.Training._
import models.services.TrainingService
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TrainingController @Inject()(
                                  cc: ControllerComponents,
                                  thisService: TrainingService,
                                  silhouette: Silhouette[DefaultEnv]
                                ) extends AbstractController(cc) {


  def Get = silhouette.SecuredAction.async { implicit request =>
    thisService.get.map(r => Ok(Json.toJson(r)))
  }

  def Create = silhouette.SecuredAction.async(parse.json){ implicit request =>
    Training.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        thisService.create(Training(0, data.name, DateTime.now(), DateTime.now(),None))
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
  def Update(id: Int) = silhouette.SecuredAction.async(parse.json){ implicit request=>
    updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        thisService.update(id, data).map( r =>Ok(Json.toJson(r)))
      }
    )
  }

  def PureDelete(id:Int) = silhouette.SecuredAction.async{ implicit request=>
    thisService.pureDelete(id).map(j=>Ok(Json.obj("result"->j)))
  }
  def PureDeleteAll = silhouette.SecuredAction.async{ implicit request=>
    thisService.pureDeleteAll.map( j=>Ok(Json.obj("result"->j)))
  }
}
