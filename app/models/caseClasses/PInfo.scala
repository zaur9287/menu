package models.caseClasses

import com.mohiva.play.silhouette.api.util.PasswordInfo
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json.Json

case class PInfo(
                  id           :Int,
                  logininfoID  :Int,
                  passwordInfo :PasswordInfo
                )

object PInfo {
  //  import play.api.libs.json.JodaWrites._
  //  import play.api.libs.json.JodaReads._
  //
  //  implicit val jsonFormat = Json.format[Interface]
  //
  //  val form = Form(
  //    tuple(
  //      "name" -> nonEmptyText,
  //      "description" -> optional(nonEmptyText)
  //    )
  //  )
}



