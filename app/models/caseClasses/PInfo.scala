package models.caseClasses

import com.mohiva.play.silhouette.api.util.PasswordInfo

case class PInfo(
                  id           :Int,
                  logininfoID  :Int,
                  passwordInfo :PasswordInfo
                )

