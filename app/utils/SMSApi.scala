package utils

import javax.inject.Inject

import models.caseClasses.SMS
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
/**
  * Created by jarvis on 5/23/16.
  */
case class SMSApi  @Inject() (ws: WSClient)
{
  val username = "technolinkyd"
  val password = "tchn4nk2"

  val username_az = "adilsms"
  val password_az = "adilsms123"

  val mock = false


  val url = "http://y.postaguvercini.com/api_ws/smsservice.asmx"
  val url_az = "http://www.poctgoyercini.com/api_http/sendsms.asp"


  def sendSms(rudeSmsSeq: Seq[GateWaySMS]): Future[Seq[GateWaySMS]] = {
    if(mock){
      Future(Seq())
    } else {
      println("send sms action call: " + rudeSmsSeq)
      if(rudeSmsSeq.isEmpty){
        Future(Nil)
      }else{
        val smsSeq: Seq[GateWaySMS] = rudeSmsSeq.map(x => x.copy(recipient = x.recipient.replace("+", "")))

        val text = GateWaySMS.normalizeString(smsSeq.head.text)

        val azeFuture = if(rudeSmsSeq.isEmpty) { Future(Seq()) } else { sendSmsToAze(rudeSmsSeq) }

        val response = for {
          azeSms <- azeFuture
        } yield azeSms
        response
      }
    }
  }

  private def sendSmsToAze(smsSeq: Seq[GateWaySMS]): Future[Seq[GateWaySMS]] = {
    val smsWithAzeRecipients = smsSeq
    val text = GateWaySMS.normalizeString(smsSeq.head.text)
    val futureSeq = smsWithAzeRecipients.map{ sms =>
      val sendSms = ws.url(url_az)
        .addQueryStringParameters("user" -> username_az)
        .addQueryStringParameters("password" -> password_az)
        .addQueryStringParameters("gsm" -> sms.recipient)
        .addQueryStringParameters("text" -> text).get()
      sendSms.map(smsSent => {
        println(smsSent.body)
        sms
      }) // sms.copy(provider_id = smsSent.body.split("&").toList.map(k => k.split("=").toList).filter(_.head == "message_id").head.last))
    }

    val emptyList = List()
    if(Future.sequence(futureSeq).equals(emptyList)){ Future(Seq()) }else{ Future.sequence(futureSeq) }
  }

}



case class GateWaySMS(
                recipient: String,
                text: String
              )
object GateWaySMS {
  def normalizeString(text: String): String = {
    text
      .replace("ə", "e")
      .replace("ı", "i")
      .replace("ö", "o")
      .replace("ğ", "g")
      .replace("ş", "sh")
      .replace("ç", "c")
      .replace("i", "i")
      .replace("ü", "u")
      .replace("Ə", "E")
      .replace("I", "I")
      .replace("Ö", "O")
      .replace("Ğ", "G")
      .replace("Ş", "Sh")
      .replace("Ç", "C")
      .replace("İ", "I")
      .replace("Ü", "U")
  }
}

