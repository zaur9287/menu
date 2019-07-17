package models.caseClasses

import com.mohiva.play.silhouette.api.LoginInfo

case class LInfo(
                 id          :Int,
                 logininfo   : LoginInfo,
                 userid      : String
                 )