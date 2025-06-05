package publicationtracker.util

import cats.effect.*
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway

object Database {

  def transactor[F[_]: Async]: Resource[F, HikariTransactor[F]] = {
    val conf = DatabaseConfig.load()

    val flywayInit = Sync[F].delay {
      val flyway = Flyway.configure()
        .dataSource(conf.url, conf.user, conf.password)
        .locations("classpath:db/migration")
        .load()

      val count = flyway.migrate()
      println(s"✅ Flyway: выполнено $count миграций.")
    }

    for {
      _ <- Resource.eval(flywayInit)

      // Только connectEC нужен для старой версии
      connectEC <- ExecutionContexts.fixedThreadPool[F](conf.poolSize)

      transactor <- HikariTransactor.newHikariTransactor[F](
        driverClassName = conf.driver,
        url             = conf.url,
        user            = conf.user,
        pass            = conf.password,
        connectEC       = connectEC
      )

      _ <- Resource.eval(Sync[F].delay(println("✅ Транзактор инициализирован.")))
    } yield transactor
  }
}
