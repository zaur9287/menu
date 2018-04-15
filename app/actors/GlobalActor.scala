package actors

import javax.inject.Singleton

import akka.actor._
import com.google.inject.Inject
import models.services.SMSService
import play.api.Logger

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Copied by Zaur on 13/04/2018.
  */

@Singleton
class GlobalActor @Inject() (sMSService: SMSService) extends Actor {
  implicit val timeout = akka.util.Timeout(30.seconds)

  def receive = {
    case "sendSMS" => sendSMS
    case _ => println("nagaysanaaa")
  }

  def sendSMS= sMSService.sendSMS

  def clean(): Unit ={
    Logger.debug("cleanup running")
  }
}
