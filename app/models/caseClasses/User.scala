package models.caseClasses

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form
import play.api.data.format.Formats._


case class User(
                  userID: UUID,
                  loginInfo: LoginInfo,
                  fullName: String,
                  email:    String,
                  avatarURL: Option[String],
                  activated: Boolean,
                  createdAt: DateTime,
                  updatedAt: DateTime,
                  companyID: Int,
                  owner: Boolean,
                  address: Option[String],
                  description: Option[String]
                 )extends Identity

object User {
  implicit val jsonFormat = Json.format[User]
}


case class UserForm (
                    email: String,
                    fullName: String,
                    avatarURL: Option[String],
                    address: Option[String],
                    description: Option[String]
                    )

object UserForm {
  implicit val jsonFormat = Json.format[UserForm]

  val formMapping = mapping(
    "email" -> nonEmptyText,
    "fullName" -> nonEmptyText,
    "avatarURL" -> optional(nonEmptyText),
    "address" -> optional(nonEmptyText),
    "description" -> optional(nonEmptyText),
  )(UserForm.apply)(UserForm.unapply)

  val form = Form(formMapping)
}