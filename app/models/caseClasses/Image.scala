package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form


case class Image(
                  id: Int,
                  path: String,
                  createdAt: DateTime,
                  updatedAt: DateTime,
                )

object Image {
  implicit val jsonFormat = Json.format[Image]
}

case class ImageForm ( path: String )
object ImageForm {
  implicit val jsonFormat = Json.format[ImageForm]
  val form = Form(mapping(
    "path" -> nonEmptyText
  )(ImageForm.apply)(ImageForm.unapply))
}