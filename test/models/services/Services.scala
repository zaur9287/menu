import models.services.GoodService
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class GoodServiceSpec extends PlaySpec with MockitoSugar {

  "GoodService#create" should {
    "this method is creating a new record " in {
      val goodService = mock[GoodService]

      val createdCompany = goodService.create(globals.TestGood.form)

      createdCompany mustBe globals.TestGood.myGood
    }
  }

  "GoodService#getAll" should {
    "returns us all goods in our db" in {
      val goodService = mock[GoodService]

      goodService.getAll mustBe globals.TestGood.seq
    }
  }

  "GoodService#update" should {
    "updating good service should return us 1 or 0 value, that means it actually updated or not" in {
      val companyService = mock[GoodService]

      val update = companyService.update(goodID = 1, globals.TestGood.form)

      update mustBe 1
    }
  }



}