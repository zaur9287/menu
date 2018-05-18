package models.caseClasses

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.data.Forms._
import play.api.data.Form


case class GoodGroup(
                       parentID: Int,
                       name: String,
                       imageID: Int,
                       createdAt: DateTime,
                       updatedAt: DateTime
                      )

object GoodGroup {
  implicit val jsonFormat = Json.format[GoodGroup]
}

//val form = Form(mapping(
//"parentID" -> number,
//"name" -> nonEmptyText,
//"imageID" -> number
//)(GoodGroups.apply)(GoodGroups.unapply))

