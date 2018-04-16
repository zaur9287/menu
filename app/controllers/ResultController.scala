package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import models.caseClasses.Result
import models.services._
import net.minidev.asm.ex.ConvertException
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import utils.auth.DefaultEnv
import utils.{GateWaySMS, Hashids, SMSApi}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ResultController @Inject()(
                                    cc: ControllerComponents,
                                    thisService: ResultService,
                                    sMSService: SMSService,
                                    wSClient: WSClient,
                                    silhouette: Silhouette[DefaultEnv]
                                  ) extends AbstractController(cc) {


  def SubmitResult(id:String) = silhouette.UnsecuredAction.async(parse.json){implicit request=>
    val hashids = new Hashids
    val smsID = hashids.decode(id)(0).toInt
    for {
      findUnsubmitted <- sMSService.findUnSubmitted(smsID)
      submit <- {
        if(findUnsubmitted){
          Result.form.bindFromRequest().fold(
            hasError=>Future(BadRequest(Json.toJson(hasError.errors.map(e=>Json.obj("key"->e.key,"message"->e.message))))),
            data =>{
              for (
                t<-sMSService.updateSubmit(smsID).map(r=>r)
              )yield t
              val result = thisService.createMultiply(data.map(d=>Result(0,smsID,d.questionID,d.answerID,false,0,0)))

              result.map( r => Ok(Json.obj("result"->r)))
            }
          )
        }else{
          Future(Ok(Json.obj("result"->0)))
        }
      }
    } yield submit
  }

  def SubmitResultTest(id:Int) = silhouette.SecuredAction.async(parse.json){implicit request=>
    val smsApi = new SMSApi(wSClient)
    for {
      sendSMS <- smsApi.sendSms(Seq(GateWaySMS("00994504733458", "Salam zaur")))
    } yield {
      Ok("hello")
    }
  }

  def allReport = silhouette.SecuredAction.async(parse.json){implicit request =>
    Result.filterForm.bindFromRequest().fold(
      hasError=>Future (BadRequest(Json.toJson(hasError.errors.map(e=>Json.obj("key"->e.key,"message"->e.message))))),
      data=>
        thisService.allReport(data.trainingID,data.categoryID,data.quizID).map(r=>Ok(Json.toJson(r)))
    )
  }

}
