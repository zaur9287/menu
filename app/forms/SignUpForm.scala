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
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "companyName" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
    * The form data.
    *
    * @param firstName The first name of a user.
    * @param lastName The last name of a user.
    * @param email The email of the user.
    * @param password The password of the user.
    * @param companyName The name of the user company
    */
  case class Data(
                   firstName: String,
                   lastName: String,
                   email: String,
                   password: String,
                   companyName: String
                 )
}
