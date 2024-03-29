package controllers

import javax.inject._
import java.util.UUID
import javax.inject.Inject
import javax.mail.internet.InternetAddress

import com.mohiva.play.silhouette
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._
import forms.SignUpForm
import models.caseClasses.{Company, User}
import models.services.{AuthTokenDAO, UserService}
import org.joda.time.DateTime
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class SignUpController @Inject()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  authTokenService: AuthTokenDAO,
  avatarService: AvatarService,
  passwordHasherRegistry: PasswordHasherRegistry,
  mailerClient: MailerClient,
  configuration: Configuration,
)(
    implicit
    assets: AssetsFinder,
    ex: ExecutionContext
  ) extends AbstractController(components) with I18nSupport {

  /**
    * Views the `Sign Up` page.
    *
    * @return The result to display.
    */
  def view = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.signUp(SignUpForm.form)))
  }

  /**
    * Handles the submitted form.
    *
    * @return The result to display.
    */
  def submit = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signUp(form))),
      data => {
        if (data.password == data.passwordConfirm) {

          val result = Redirect(routes.SignUpController.view()).flashing("info" -> Messages("sign.up.email.sent", data.email))
          val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              val url = routes.SignInController.view().absoluteURL()
              mailerClient.send(Email(
                subject = Messages("email.already.signed.up.subject"),
                from = Messages("email.from"),
                to = Seq(data.email),
                bodyText = Some(views.txt.emails.alreadySignedUp(user, url).body),
                bodyHtml = Some(views.html.emails.alreadySignedUp(user, url).body)
              ))

              val c = configuration.underlying
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              silhouette.env.authenticatorService.create(user.loginInfo).map {
                case authenticator =>
                  authenticator
              }.flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService.init(authenticator).flatMap { token =>
                  Future(Ok(Json.toJson(token)))
                }
              }
            case None =>
              val authInfo = passwordHasherRegistry.current.hash(data.password)
              val user = User(
                userID = UUID.randomUUID(),
                loginInfo = loginInfo,
                fullName = data.fullName,
                email = data.email,
                avatarURL = None,
                activated = true,
                createdAt = DateTime.now,
                updatedAt = DateTime.now,
                companyID = 0,
                owner = false,
                address = None,
                description = None
              )
              val company = Company(
                id = 0,
                name = data.companyName,
                description = None,
                imageID = 0,
                createdAt = DateTime.now(),
                updatedAt = DateTime.now()
              )
              for {
                avatar <- avatarService.retrieveURL(data.email)
                user <- userService.save(user.copy(avatarURL = avatar), company)
                authInfo <- authInfoRepository.add(loginInfo, authInfo)
                authToken <- authTokenService.create(user.userID)
              } yield {

                silhouette.env.eventBus.publish(SignUpEvent(user, request))

              }
              val c = configuration.underlying
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              silhouette.env.authenticatorService.create(user.loginInfo).map {
                case authenticator =>
                  authenticator
              }.flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService.init(authenticator).flatMap { token =>
                  Future(Ok(Json.toJson(token)))
                }
              }
          }
        } else {
          Future(BadRequest(Json.obj("key" -> "", "message" -> "Confirm password error!")))
        }
      }
    )
  }
}
