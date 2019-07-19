package globals

import org.joda.time.DateTime
import models.caseClasses._


object TestCompany{
  def myCompany = Company(
    id = 1,
    name = "my company",
    description = Some("some descriptions are here"),
    imageID = 0,
    createdAt = DateTime.now,
    updatedAt = DateTime.now
  )

  def seq = Seq(myCompany, myCompany)

  def form = CompanyForm(
    name = "my company form",
    description = Some("some descriptions are here"),
    imageID = 0
  )
}


object TestGood{
  def myGood = Good(
    id = 1,
    groupID = 1,
    companyID = 1,
    name = "my good",
    description = Some("some descriptions are here"),
    price = 1.50d,
    quantity = 5,
    imageID = 0,
    createdAt = DateTime.now,
    updatedAt = DateTime.now
  )

  def seq = Seq(myGood, myGood)

  def form =
  GoodForm(
    groupID = 1,
    companyID = 1,
    name = "my good form",
    description = Some("Some descriptions are here"),
    price = 5.2d,
    quantity = 5,
    imageID = 1
  )
}