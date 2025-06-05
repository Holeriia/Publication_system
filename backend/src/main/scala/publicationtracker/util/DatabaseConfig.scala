package publicationtracker.util

import com.typesafe.config.ConfigFactory

final case class DbConfig(
                           url: String,
                           user: String,
                           password: String,
                           driver: String,
                           poolSize: Int
                         )

object DatabaseConfig {
  private val config = ConfigFactory.load().getConfig("db")

  def load(): DbConfig = DbConfig(
    url = config.getString("url"),
    user = config.getString("user"),
    password = config.getString("password"),
    driver = config.getString("driver"),
    poolSize = config.getInt("poolSize")
  )
  
}
