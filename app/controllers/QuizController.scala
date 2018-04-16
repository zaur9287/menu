package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.Quiz
import models.caseClasses.Quiz._
import models.services.QuizService
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class QuizController @Inject()(
                                    cc: ControllerComponents,
                                    thisService: QuizService,
                                    silhouette: Silhouette[DefaultEnv]
                                  ) extends AbstractController(cc) {


  def Get = silhouette.SecuredAction.async { implicit request =>
    thisService.get.map(r => Ok(Json.toJson(r)))
  }
  def getQuestions(id:Int) = silhouette.SecuredAction.async { implicit request =>
    thisService.getQuestions(id).map(r =>
      Ok(Json.toJson(r))
    )
  }

  def Create = silhouette.SecuredAction.async(parse.json){ implicit request =>
    Quiz.form.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        thisService.create(Quiz(0, data.name,data.spiker,data.trainingID,data.categoryID, DateTime.now(), DateTime.now(),None))
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

  def getIDS = silhouette.SecuredAction.async(parse.json){implicit request=>
    Quiz.searchForm.bindFromRequest().fold(
      hasErr=>Future(BadRequest(Json.toJson(hasErr.errors.map(e=>Json.obj("key"->e.key,"message"->e.message))))),
      data=>{
        thisService.findBySearchForm(data.trainingId,data.categoryId)
          .map(r=>Ok(Json.toJson(r)))
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

  def getByCategoryID(id:Int) = silhouette.SecuredAction.async {implicit request=>
    thisService.findByCategoryID(id).map(r=>Ok(Json.toJson(r)))
  }

  def getByTrainingID(id:Int) = silhouette.SecuredAction.async {implicit request=>
    thisService.findByTrainingID(id).map(r=>Ok(Json.toJson(r)))
  }

  def Update(id: Int) = silhouette.SecuredAction.async(parse.json){ implicit request=>
    updateForm.bindFromRequest().fold(
      formWithErrors => Future(BadRequest(Json.toJson(formWithErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data => {
        thisService.update(id, data).map( r =>
          if(r.isDefined){
            Ok(Json.toJson(r.get))
          } else {
            BadRequest(Json.obj("status" -> "KO", "message" -> "could not create data."))
          }
        )
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
