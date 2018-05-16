package modules

import javax.inject.Named

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.{AbstractModule, Inject}
import play.api.Environment
import play.libs.akka.AkkaGuiceSupport

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class TasksScheduleModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {
    bindActor(classOf[GlobalActor], "scheduler-actor")
    bind(classOf[Scheduler]).asEagerSingleton()
  }
}

class Scheduler @Inject() (val env: Environment, val system: ActorSystem, @Named("scheduler-actor") val schedulerActor: ActorRef)(implicit ec: ExecutionContext)
{
  system.scheduler.schedule(30.seconds, 30.seconds, schedulerActor, "sendSMS")
}
