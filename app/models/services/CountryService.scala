package models.services

import com.google.inject.ImplementedBy
import javax.inject.Inject

import models.caseClasses.Country
import models.caseClasses.CountryForms.UpdateCountryForm
import models.daos.CountriesDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[CountriesDAOServiceImpl])
trait CountryService {
  def get                                                   :Future[Seq[Country]]
  def create(Country: Country)                              :Future[Option[Country]]
  def pureDelete(id:Int)                                    :Future[Int]
  def pureDeleteAll                                         :Future[Int]
  def delete(selectedID:Int)                                :Future[Int]
  def deleteAll                                             :Future[Int]
  def update(id: Int, updateCountryForm: UpdateCountryForm) :Future[Option[Country]]
  def findCountryByID(id: Int)                              :Future[Option[Country]]
}

class CountriesDAOServiceImpl @Inject()(CountriesDAO: CountriesDAO) extends CountryService {
  override def get: Future[Seq[Country]] = {
    for {
      allCountries <- CountriesDAO.get
    } yield {
      allCountries
    }
  }
  override def create(Country: Country): Future[Option[Country]]          = CountriesDAO.create(Country)
  override def delete(selectedID:Int): Future[Int]                        = CountriesDAO.delete(selectedID)
  override def deleteAll: Future[Int]                                     = CountriesDAO.deleteAll
  override def update(id: Int, updateCountryForm: UpdateCountryForm): Future[Option[Country]] = CountriesDAO.update(id, updateCountryForm)
  override def findCountryByID(id:Int):Future[Option[Country]]            = CountriesDAO.findCountryByID(id)
  override def pureDelete(id:Int):Future[Int]                             = CountriesDAO.pureDelete(id)
  override def pureDeleteAll:Future[Int]                                  = CountriesDAO.pureDeleteAll
}
