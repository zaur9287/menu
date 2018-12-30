package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * The form which handles the sign up process.
  * Modified by Zaur Qabala
  */
object SignUpForm {

  /**
    * A modified play framework form.
    */
  val form = Form(
    mapping(
      "fullName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "passwordConfirm" -> nonEmptyText,
      "companyName" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
    * The form data.
    *
    * @param fullName The full name of a user.
    * @param email The email of the user.
    * @param password The password of the user.
    * @param passwordConfirm The confirming password of the user.
    * @param companyName The name of the user company
    */
  case class Data(
                   fullName: String,
                   email: String,
                   password: String,
                   passwordConfirm: String,
                   companyName: String
                 )
}
