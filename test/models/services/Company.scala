import models.services.CompanyService
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class CompanyServiceSpec extends PlaySpec with MockitoSugar {

  "CompanyService#create" should {
    "returns us the Company " in {
      val companyService = mock[CompanyService]

      val createdCompany = companyService.create(globals.TestCompany.form)

      createdCompany mustBe globals.TestCompany.myCompany
    }
  }

  "CompanyService#getAll" should {
    "returns us all Companies in our db" in {
      val companyService = mock[CompanyService]

      companyService.getAll mustBe globals.TestCompany.seq
    }
  }

  "CompanyService#update" should {
    "updating company service should return us 1 or 0 value, that means it actually updated or not" in {
      val companyService = mock[CompanyService]

      val update = companyService.update(companyID = 1, globals.TestCompany.form)

      update mustBe 1
    }
  }



}