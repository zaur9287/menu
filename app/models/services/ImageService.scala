package models.services

import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.caseClasses.Image
import models.daos.ImageDAO

import scala.concurrent.Future

@ImplementedBy(classOf[ImageServiceImpl])
trait ImageService {
  def getAll: Future[Seq[Image]]


}


class ImageServiceImpl @Inject() (imageDAO: ImageDAO) extends ImageService{
  override def getAll: Future[Seq[Image]] = imageDAO.getAll

}