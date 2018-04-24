package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.caseClasses._
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.PostgresProfile
import slick.lifted.ProvenShape
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions extends HasDatabaseConfigProvider[PostgresProfile] {
  import com.github.tototoshi.slick.PostgresJodaSupport._
  import profile.api._
  //result count for per page
  var resultCount:Int = 20
  def calculateMaxPageNum(all:Int):Int = if (all%resultCount>0) all/resultCount+1 else all/resultCount
  //**************Client table***************************************************************************************
  case class DBClient(
                     id: Int,
                     name: String,
                     desc: Option[String],
                     expireDate: DateTime,
                     createdAt: DateTime,
                     updatedAt: DateTime,
                     deletedAt: Option[DateTime]
                     ){
    def toClients: Client = Client(id, name, desc, expireDate, createdAt, updatedAt)
    def isExpired: Boolean = DateTime.now().isAfter(expireDate)
  }

  class Clients(tag: Tag) extends Table[DBClient](tag, "clients"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def desc = column[Option[String]]("description")
    def expire_date = column[DateTime]("expiry_date")
    def created_at = column[DateTime]("created_at")
    def updated_at = column[DateTime]("updated_at")
    def deleted_at = column[Option[DateTime]]("deleted_at")

    override def * = (id, name, desc, expire_date, created_at, updated_at, deleted_at) <> (DBClient.tupled, DBClient.unapply)
  }

  val slickClients = TableQuery[Clients]

  //**************Client contacts table***************************************************************************************
  case class DBContact(
                       id: Int,
                       client_id: Int,
                       mobile: String,
                       desc: String,
                       createdAt: DateTime,
                       updatedAt: DateTime,
                       deletedAt: Option[DateTime]
                     ){
    def toContacts: Contact = Contact(id,client_id, mobile, desc, createdAt, updatedAt)
  }

  class Contacts(tag: Tag) extends Table[DBContact ](tag, "contacts"){
    def id            = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def client_id     = column[Int]("client_id")
    def mobile        = column[String]("mobile")
    def desc          = column[String]("description")
    def created_at    = column[DateTime]("created_at")
    def updated_at    = column[DateTime]("updated_at")
    def deleted_at    = column[Option[DateTime]]("deleted_at")

    override def * = (id, client_id, mobile, desc, created_at, updated_at, deleted_at) <> (DBContact.tupled, DBContact.unapply)
  }

  val slickContacts= TableQuery[Contacts]


  //**************Company table***************************************************************************************
  case class DBCompany(
                       id: Int,
                       name: String,
                       desc: Option[String],
                       createdAt: DateTime,
                       updatedAt: DateTime,
                       deletedAt: Option[DateTime]
                     ){
    def toCompanies: Company= Company(id, name, desc, createdAt, updatedAt)
  }

  class Companies(tag: Tag) extends Table[DBCompany](tag, "companies"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def desc = column[Option[String]]("description")
    def created_at = column[DateTime]("created_at")
    def updated_at = column[DateTime]("updated_at")
    def deleted_at = column[Option[DateTime]]("deleted_at")

    override def * = (id, name, desc, created_at, updated_at, deleted_at) <> (DBCompany.tupled, DBCompany.unapply)
  }

  val slickCompanies = TableQuery[Companies]

  //**************Country table***************************************************************************************
  case class DBCountry(
                        id: Int,
                        name: String,
                        desc: Option[String],
                        createdAt: DateTime,
                        updatedAt: DateTime,
                        deletedAt: Option[DateTime]
                      ){
    def toCountries: Country= Country(id, name, desc, createdAt, updatedAt)
  }

  class Countries(tag: Tag) extends Table[DBCountry](tag, "countries"){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def desc = column[Option[String]]("description")
    def created_at = column[DateTime]("created_at")
    def updated_at = column[DateTime]("updated_at")
    def deleted_at = column[Option[DateTime]]("deleted_at")

    override def * = (id, name, desc, created_at, updated_at, deleted_at) <> (DBCountry.tupled, DBCountry.unapply)
  }

  val slickCountries = TableQuery[Countries]

  //**************Interface table***************************************************************************************
  case class DBInterface(
                        id: Int,
                        name: String,
                        desc: Option[String],
                        createdAt: DateTime,
                        updatedAt: DateTime,
                        deletedAt: Option[DateTime]
                      ){
    def toInterfaces: Interface= Interface(id, name, desc, createdAt, updatedAt)
  }

  class Interfaces(tag: Tag) extends Table[DBInterface](tag, "interfaces"){
    def id              = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name            = column[String]("name")
    def desc            = column[Option[String]]("description")
    def created_at      = column[DateTime]("created_at")
    def updated_at      = column[DateTime]("updated_at")
    def deleted_at      = column[Option[DateTime]]("deleted_at")

    override def * = (id, name, desc, created_at, updated_at, deleted_at) <> (DBInterface.tupled, DBInterface.unapply)
  }

  val slickInterfaces = TableQuery[Interfaces]




  //**************silhouette tables   user***************************************************************************************
  case class DBUser (
                      userID: String,
                      fullName: String,
                      email: String,
                      avatarURL: Option[String],
                      activated: Boolean
                    ){
    def toUser:User = User(UUID.fromString(userID),LoginInfo("credentials",email),fullName,email,avatarURL,activated)

  }

  class Users(tag: Tag) extends Table[DBUser](tag, "users") {
    def id          = column[String]("userid", O.PrimaryKey)
    def fullName    = column[String]("fullname")
    def email       = column[String]("email")
    def avatarURL   = column[Option[String]]("avatarurl")
    def activated   = column[Boolean]       ("activated")
    def * = (id, fullName, email, avatarURL,activated) <> (DBUser.tupled, DBUser.unapply)
  }

  val slickUsers = TableQuery[Users]

  //**************silhouette tables   user***************************************************************************************
  case class DBToken (
                      ID: String,
                      userID: String,
                      expiry: DateTime
                      )

  class Tokens(tag: Tag) extends Table[DBToken](tag, "tokens") {
    def id          = column[String]("id", O.PrimaryKey, O.AutoInc)
    def userID      = column[String]("userid")
    def expiry      = column[DateTime]("expiry")
    def * = (id, userID, expiry) <> (DBToken.tupled, DBToken.unapply)
  }

  val slickTokens = TableQuery[Tokens]

  //**************silhouette tables   logininfo***************************************************************************************
  case class DBLoginInfos(
                          id          :Int,
                          providerid  : String,
                          providerkey : String,
                          userid      : String
                        ){
    def toLInfo: LInfo= LInfo(id,LoginInfo(providerid,providerkey),userid)
  }

  class LoginInfos(tag: Tag) extends Table[DBLoginInfos](tag, "logininfo"){
    def id                       = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def providerid               = column[String]("providerid")
    def providerkey              = column[String]("providerkey")
    def userid                   = column[String]("userid")

    override def * = (id,providerid, providerkey, userid) <> (DBLoginInfos.tupled, DBLoginInfos.unapply)
  }

  val slickLoginInfos = TableQuery[LoginInfos]


  //**************silhouette tables   password info ***************************************************************************************
  case class DBPasswordInfos(
                           id           : Int,
                           logininfoID  : Int,
                           hasher       : String,
                           password     : String,
                           salt         : Option[String]
                         ){
    def toPInfo: PInfo= PInfo(id,logininfoID,PasswordInfo(hasher,password,salt))
  }

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfos](tag, "passwordinfo"){
    def id              = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def logininfoID     = column[Int]("logininfo_id")
    def hasher          = column[String]("hasher")
    def password        = column[String]("password")
    def salt            = column[Option[String]]("salt")

    override def * = (id,logininfoID,hasher,password,salt) <> (DBPasswordInfos.tupled, DBPasswordInfos.unapply)
  }

  val slickPasswordInfos = TableQuery[PasswordInfos]



  //**************first project tables ***************************************************************************************

  //**************participant***************************************************************************************
  case class DBParticipants(
                              id              : Int,
                              name            : String,
                              phone           : String,
                              company         : String,
                              categoryID      : Int,
                              createdAt       : DateTime,
                              updatedAt       : DateTime,
                              deletedAt       : Option[DateTime]
                            ){
    def toParticipant: Participant= Participant(id,name,phone,company,categoryID,createdAt,updatedAt,deletedAt)
  }

  class Participants(tag: Tag) extends Table[DBParticipants](tag, "participant"){
    def id                 = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name               = column[String]("name")
    def phone              = column[String]("phone")
    def company            = column[String]("company")
    def categoryID         = column[Int]("category_id")
    def createdAt          = column[DateTime]("created_at")
    def updatedAt          = column[DateTime]("updated_at")
    def deletedAt          = column[Option[DateTime]]("deleted_at")

    override def * = (id,name,phone,company,categoryID,createdAt,updatedAt,deletedAt) <> (DBParticipants.tupled, DBParticipants.unapply)
  }

  val slickParticipants= TableQuery[Participants]

  //**************training***************************************************************************************
  case class DBTraining(
                             id              : Int,
                             name            : String,
                             createdAt       : DateTime,
                             updatedAt       : DateTime,
                             deletedAt       : Option[DateTime]
                           ){
    def toTraining: Training= Training(id,name,createdAt,updatedAt,deletedAt)
  }

  class Trainings(tag: Tag) extends Table[DBTraining](tag, "training"){
    def id                 = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name               = column[String]("name")
    def createdAt          = column[DateTime]("created_at")
    def updatedAt          = column[DateTime]("updated_at")
    def deletedAt          = column[Option[DateTime]]("deleted_at")

    override def * = (id,name,createdAt,updatedAt,deletedAt) <> (DBTraining.tupled, DBTraining.unapply)
  }

  val slickTrainings= TableQuery[Trainings]

  //**************category***************************************************************************************
  case class DBCategories(
                          id              : Int,
                          name            : String,
                          createdAt       : DateTime,
                          updatedAt       : DateTime,
                          deletedAt       : Option[DateTime]
                        ){
    def toCategory: Category= Category(id,name,createdAt,updatedAt,deletedAt)
  }

  class Categories(tag: Tag) extends Table[DBCategories](tag, "category"){
    def id                 = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name               = column[String]("name")
    def createdAt          = column[DateTime]("created_at")
    def updatedAt          = column[DateTime]("updated_at")
    def deletedAt          = column[Option[DateTime]]("deleted_at")

    override def * = (id,name,createdAt,updatedAt,deletedAt) <> (DBCategories.tupled, DBCategories.unapply)
  }

  val slickCategories= TableQuery[Categories]



  //**************quiz***************************************************************************************
  case class DBQuizzes(
                             id              : Int,
                             name            : String,
                             spiker          : String,
                             trainingID      : Int,
                             categoryID      : Int,
                             createdAt       : DateTime,
                             updatedAt       : DateTime,
                             deletedAt       : Option[DateTime]
                           ){
    def toQuiz: Quiz= Quiz(id,name,spiker,trainingID,categoryID,createdAt,updatedAt,deletedAt)
  }

  class Quizzes(tag: Tag) extends Table[DBQuizzes](tag, "quiz"){
    def id                 = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name               = column[String]("name")
    def spiker             = column[String]("spiker")
    def trainingID         = column[Int]("training_id")
    def categoryID         = column[Int]("category_id")
    def createdAt          = column[DateTime]("created_at")
    def updatedAt          = column[DateTime]("updated_at")
    def deletedAt          = column[Option[DateTime]]("deleted_at")

    override def * = (id,name,spiker,trainingID,categoryID,createdAt,updatedAt,deletedAt) <> (DBQuizzes.tupled, DBQuizzes.unapply)
  }

  val slickQuizzes= TableQuery[Quizzes]


  //**************question***************************************************************************************
  case class DBQuestions(
                        id              : Int,
                        text            : String,
                        weight          : Int,
                        quizID          : Int,
                        createdAt       : DateTime,
                        updatedAt       : DateTime,
                        deletedAt       : Option[DateTime]
                      ){
    def toQuestion: Question= Question(id,text,weight,quizID,createdAt,updatedAt,deletedAt)
  }

  class Questions(tag: Tag) extends Table[DBQuestions](tag, "question"){
    def id                 = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def text               = column[String]("text")
    def weight             = column[Int]("weight")
    def quizID             = column[Int]("quiz_id")
    def createdAt          = column[DateTime]("created_at")
    def updatedAt          = column[DateTime]("updated_at")
    def deletedAt          = column[Option[DateTime]]("deleted_at")

    override def * = (id,text,weight,quizID,createdAt,updatedAt,deletedAt) <> (DBQuestions.tupled, DBQuestions.unapply)
  }

  val slickQuestions= TableQuery[Questions]

  //**************answer***************************************************************************************
  case class DBAnswers(
                          id              : Int,
                          text            : String,
                          correct         : Boolean,
                          questionID      : Int,
                          createdAt       : DateTime,
                          updatedAt       : DateTime,
                          deletedAt       : Option[DateTime]
                        ){
    def toAnswer: Answer= Answer(id,text,correct,questionID,createdAt,updatedAt,deletedAt)
  }

  class Answers(tag: Tag) extends Table[DBAnswers](tag, "answer"){
    def id                 = column[Int]    ("id", O.PrimaryKey, O.AutoInc)
    def text               = column[String] ("text")
    def correct            = column[Boolean]("correct")
    def questionID         = column[Int]    ("question_id")
    def createdAt         = column[DateTime]("created_at")
    def updatedAt         = column[DateTime]("updated_at")
    def deletedAt         = column[Option[DateTime]]("deleted_at")

    override def * = (id,text,correct,questionID,createdAt,updatedAt,deletedAt) <> (DBAnswers.tupled, DBAnswers.unapply)
  }

  val slickAnswers= TableQuery[Answers]

  //**************sms***************************************************************************************
  case class DBSMS(
                        id              : Int,
                        participantID   : Int,
                        trainingID      : Int,
                        categoryID      : Int,
                        quizID          : Int,
                        status          : String,
                        opened          : Option[DateTime],
                        submitted       : Option[DateTime],
                      ){
    def toSMS:SMS = SMS(id,participantID,trainingID,categoryID,quizID,status,opened,submitted)

  }

  class SMSs(tag: Tag) extends Table[DBSMS](tag, "sms"){
    def id                 = column[Int]    ("id", O.PrimaryKey, O.AutoInc)
    def participantID      = column[Int]    ("participant_id")
    def trainingID         = column[Int]    ("training_id")
    def categoryID         = column[Int]    ("category_id")
    def quizID             = column[Int]    ("quiz_id")
    def status             = column[String] ("status")
    def opened             = column[Option[DateTime]]("opened")
    def submitted          = column[Option[DateTime]]("submitted")

    override def * = (id,participantID,trainingID,categoryID,quizID,status,opened,submitted) <> (DBSMS.tupled, DBSMS.unapply)
  }

  val slickSMS= TableQuery[SMSs]

  //**************result***************************************************************************************
  case class DBResult(
                    id              : Int,
                    SMSID           : Int,
                    questionID      : Int,
                    answerID        : Int,
                    correct         : Boolean,
                    weight          : Int,
                    response        : Int
                  ){
    def toResult:Result = Result(id,SMSID,questionID,answerID,correct,weight,response)

  }

  class Results(tag: Tag) extends Table[DBResult](tag, "result"){
    def id                 = column[Int]    ("id", O.PrimaryKey, O.AutoInc)
    def SMSID              = column[Int]    ("sms_id")
    def questionID         = column[Int]    ("question_id")
    def answerID           = column[Int]    ("answer_id")
    def correct            = column[Boolean]("correct")
    def weight             = column[Int]    ("weight")
    def response           = column[Int]    ("response")

    override def * = (id,SMSID,questionID,answerID,correct,weight,response) <> (DBResult.tupled, DBResult.unapply)
  }

  val slickResult= TableQuery[Results]




}
