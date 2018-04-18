package models.daos

import org.joda.time.DateTime
import com.google.inject.{ImplementedBy, Inject}
import models.caseClasses.Country
import models.caseClasses.CountryForms.UpdateCountryForm
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CountriesDAOImpl])
trait CountriesDAO {
  def get: Future[Seq[Country]]
  def create(Country: Country): Future[Option[Country]]
  def pureDelete(id:Int): Future[Int]
  def pureDeleteAll: Future[Int]
  def delete(selectedID:Int): Future[Int]
  def deleteAll: Future[Int]
  def update(id:Int,updateCountryForm: UpdateCountryForm): Future[Int]
  def findCountryByID(Countryid: Int): Future[Option[Country]]
}

class CountriesDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)  extends CountriesDAO with DBTableDefinitions {
  import profile.api._
  import com.github.tototoshi.slick.PostgresJodaSupport._

  override def get: Future[Seq[Country]] = {
    val query = slickCountries.filter(r => r.deleted_at.isEmpty)
    db.run(query.result).map( r =>
      r.map(_.toCountries).sortBy(_.id)
    )
  }
//bir sətrin yaradılmasıı və yaranmış sətrin geri qaytarılması
  override def create(Country: Country): Future[Option[Country]] = {
    val dBCountry = DBCountry(Country.id, Country.name, Country.description, Country.createdAt, Country.updatedAt, None)
    val query = slickCountries.returning(slickCountries) += dBCountry
    db.run(query).map ( r => Some(r.toCountries) )
  }
//id-yə görə birinin silinməsi (seçilən sətr bazan silinir.)
  override def delete(selectedID:Int): Future[Int] = {
    val selectedCountry= slickCountries.filter(_.id===selectedID)
    val deleteAction = selectedCountry.delete
    val affectedRowsCount:Future[Int] = db.run(deleteAction)
    affectedRowsCount
  }
//bütün sətirlər bazadan silinir.
  override def deleteAll: Future[Int] = {
    val selectAllCountries = slickCountries
    val deletingAllAction = selectAllCountries.delete
    val affectedRowsCount:Future[Int] = db.run(deletingAllAction)
    affectedRowsCount
  }

  override  def update(id:Int,updateCountryForm: UpdateCountryForm): Future[Int] = {
    val updateQuery = slickCountries.filter(c => c.id === id && c.deleted_at.isEmpty)
      .map(c => (c.name, c.desc, c.updated_at))
      .update((updateCountryForm.name, Some(updateCountryForm.description), DateTime.now))
    db.run(updateQuery)
 }

  override def findCountryByID(Countryid:Int): Future[Option[Country]] = {
    val query = slickCountries.filter(f=>f.id === Countryid && f.deleted_at.isEmpty).result
    db.run(query.headOption).map(_.map(_.toCountries))
  }
//id-yə görə birinin silinməsi (bazada silinmə tarixinin update olunması)
  override def pureDelete(id:Int): Future[Int] = {
    val query = slickCountries.filter(f=>f.id === id && f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
//bütün sətrlərdə silinmə tarixini update olunması
  override def pureDeleteAll: Future[Int] = {
    val query = slickCountries.filter(f=>f.deleted_at.isEmpty).map(c=>(c.deleted_at)).update(Some(DateTime.now))
    db.run(query).map(r=>r)
  }
}


