package models.caseClasses

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json.Json


case class User(
                  userID: UUID,
                  loginInfo: LoginInfo,
                  fullName: String,
                  email:    String,
                  avatarURL: Option[String],
                  activated: Boolean
                 )extends Identity

object User {
//  import play.api.libs.json.JodaWrites._
//  import play.api.libs.json.JodaReads._

  //implicit val jsonFormat = Json.format[User]

//  val form = Form(
//    tuple(
//      "name" -> nonEmptyText,
//      "description" -> optional(nonEmptyText)
//    )
//  )
}



//
//object  UserForms {
//
//  // User update form
//  case class UpdateUserForm(
//                               name:String,
//                               description: String
//                             ){
//    def tupled = (name, description)
//  }
//
//  object UpdateUserForm {
//    implicit val jsonFormat = Json.format[UpdateUserForm]
//  }
//
//  val updateForm = Form(
//    mapping(
//      "name" -> text,
//      "description" -> text
//    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
//  )
//
//}