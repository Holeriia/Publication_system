package publicationtracker.util

import cats.effect.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import doobie.*
import doobie.hikari.*
import doobie.implicits.*
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Database {

  def transactor[F[_]: Async]: Resource[F, HikariTransactor[F]] = {
    val conf = DatabaseConfig.load()
    val dbName = conf.url.split("/").lastOption.getOrElse("publication_db")

    for {
      // Инициализируем логгер из log4cats
      given Logger[F] <- Resource.eval(Slf4jLogger.create[F])

      // 1. Проверяем и создаём базу данных, если её нет
      _ <- Resource.eval(ensureDatabaseExists[F](conf, dbName))

      // 2. Запускаем миграции Flyway
      _ <- Resource.eval(runMigrations[F](conf))

      // 3. Создаём ExecutionContext для подключения к БД
      connectEC <- ExecutionContexts.fixedThreadPool[F](conf.poolSize)

      // 4. Создаём HikariTransactor
      transactor <- HikariTransactor.newHikariTransactor[F](
        driverClassName = conf.driver,
        url             = conf.url,
        user            = conf.user,
        pass            = conf.password,
        connectEC       = connectEC
      )

      // 5. Логируем успешную инициализацию транзактора
      _ <- Resource.eval(Logger[F].info("✅ Транзактор инициализирован."))
    } yield transactor
  }

  import cats.effect.Async
  import doobie.Fragment
  import org.typelevel.log4cats.Logger

  private def ensureDatabaseExists[F[_]: Async](conf: DbConfig, dbName: String)(using Logger[F]): F[Unit] = {
    val adminUrl = conf.url
      .replaceFirst(s"/" + dbName, "/postgres") +
      s"?user=${conf.user}&password=${conf.password}"

    val xa = Transactor.fromDriverManager[F](
      driver = conf.driver,
      url = adminUrl,
      logHandler = None
    )

    val checkExists =
      sql"SELECT 1 FROM pg_database WHERE datname = $dbName"
        .query[Int]
        .option
        .map(_.isDefined)

    for {
      _ <- Logger[F].info(s"🔍 Проверка наличия базы данных '$dbName'")
      exists <- checkExists.transact(xa)
      _ <- if (exists) {
        Logger[F].info(s"✅ База данных '$dbName' уже существует.")
      } else {
        for {
          _ <- Logger[F].warn(s"⚠️ База данных '$dbName' не найдена. Создаём...")
          _ <- Async[F].delay {
            // Выполняем create database напрямую через JDBC без транзакции
            val conn = java.sql.DriverManager.getConnection(adminUrl, conf.user, conf.password)
            try {
              val stmt = conn.createStatement()
              // Обязательно экранируем имя базы в двойные кавычки
              stmt.executeUpdate(s"""CREATE DATABASE "$dbName"""")
              stmt.close()
            } finally conn.close()
          }
          _ <- Logger[F].info(s"✅ База данных '$dbName' создана.")
        } yield ()
      }
    } yield ()
  }


  private def runMigrations[F[_]: Sync](conf: DbConfig): F[Unit] = Sync[F].delay {
    val flyway = Flyway.configure()
      .dataSource(conf.url, conf.user, conf.password)
      .locations("classpath:db/migration")
      .load()

    val count = flyway.migrate()
    println(s"✅ Flyway: выполнено $count миграций.")
  }
}
