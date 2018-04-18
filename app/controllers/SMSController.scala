package controllers


import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.SMS
import models.caseClasses.SMS._
import models.services.SMSService
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import actors.HelloActor
import akka.actor.ActorSystem
import net.minidev.asm.ex.ConvertException
import play.api.libs.ws.WSClient
import utils.{GateWaySMS, Hashids, SMSApi}

@Singleton
class SMSController @Inject()(
                                    cc: ControllerComponents,
                                    thisService: SMSService,
                                    smsService: SMSService,
                                    silhouette: Silhouette[DefaultEnv],
                                    acSystem:ActorSystem,
                                    wSClient: WSClient
                                  ) extends AbstractController(cc) {

  def OpenedSMS(id:Int) = silhouette.SecuredAction.async { implicit  request =>
      thisService.updateOpened(id).map(r=> Ok(Json.obj("result"->r)))
  }
  def SubmitPressed(id:Int) = silhouette.UnsecuredAction.async{ implicit  request =>
      thisService.findUnSubmitted(id).map(r=> Ok(Json.obj("result"->r)))
  }

  def sendMessages= silhouette.SecuredAction.async(parse.json){ implicit  request =>
    SMS.sentForm.bindFromRequest().fold(
      hasErrors=>Future(BadRequest(Json.toJson(hasErrors.errors.map(e => Json.obj("key" -> e.key, "message" -> e.message))))),
      data=>{
        val t = thisService.createRows(data.categoryID,data.quizID).map(r=>r)
        val tt = thisService.sendSMS
        tt.map(r=>Ok(Json.obj("result"->r)))
      }
    )
  }
  def updateOpened(id:String) = silhouette.UnsecuredAction.async{implicit request=>
    val hashids = new Hashids
    val smsID = hashids.decode(id)(0).toInt
      thisService.updateOpened(smsID).map(r=> Ok(Json.obj("result"->r)))
  }

  def getQuiz(id: String) = silhouette.UnsecuredAction.async{ implicit request=>
    thisService.getQuiz(id).map(r=> Ok(Json.obj("result"->r)))
  }

  def unsentMessages = silhouette.SecuredAction.async {implicit request=>
    thisService.unsentMessages.map(r=>Ok(Json.toJson(r)))
  }



}
