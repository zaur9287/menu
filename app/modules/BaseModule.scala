package modules

import com.google.inject.AbstractModule
import models.daos.{ TokensDAO,TokensDAOImpl}
import models.services.{ AuthTokenDAO, AuthTokenDAOImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * The base Guice module.
  */
class BaseModule extends AbstractModule with ScalaModule {

  /**
    * Configures the module.
    */
  def configure(): Unit = {
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[TokensDAO].to[TokensDAOImpl]
  }
}
