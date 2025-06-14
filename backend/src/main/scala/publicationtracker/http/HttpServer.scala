package publicationtracker.http

import cats.effect.{Async, Sync}
import cats.syntax.all.*
import doobie.hikari.HikariTransactor
import fs2.io.file.Files
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.http4s.server.staticcontent.ResourceServiceBuilder
import org.http4s.{HttpApp, HttpRoutes, StaticFile}
import publicationtracker.http.routes.{EmployeeRoutes, ReferenceRoutes}
import publicationtracker.repository.impl.*
import publicationtracker.service.impl.{AchieventServiceImpl, EmployeeServiceImpl}

object HttpServer {

  def httpApp[F[_]: Async](transactor: HikariTransactor[F]): F[HttpApp[F]] = {
    implicit val filesF: Files[F] = Files.forAsync[F]

    val dsl = new Http4sDsl[F] {}
    import dsl.*

    for {
      achievementTypeRepo <- Sync[F].delay(new AchievementTypeRepository[F](transactor))
      maybeOtherType <- achievementTypeRepo.findByName("другое")
      otherTypeId <- maybeOtherType match {
        case Some(t) => Sync[F].pure(t.id)
        case None    => Sync[F].raiseError(new Exception("Тип достижения 'другое' не найден в базе данных!"))
      }

      // Правильно создаём реализацию репозитория AchieventRepositoryImpl
      achieventRepo = new AchieventRepositoryImpl[F](transactor)
      achieventService = new AchieventServiceImpl[F](achieventRepo, achievementTypeRepo)

      cityRepo = new CityRepository[F](transactor)
      cityApiRoutes = new ReferenceRoutes[F]("city", cityRepo).routes

      employeeRepo = new EmployeeRepositoryImpl[F](transactor)
      degreeRepo = new AcademicDegreeRepository[F](transactor)
      titleRepo = new AcademicTitleRepository[F](transactor)
      postRepo = new EmployeePostRepository[F](transactor)

      employeeService = new EmployeeServiceImpl[F](
        employeeRepo,
        degreeRepo,
        titleRepo,
        postRepo,
        achieventService
      )
      employeeRoutes = new EmployeeRoutes[F](employeeService).routes
      staticRoutes = ResourceServiceBuilder[F]("/").toRoutes

      fallbackRoute: HttpRoutes[F] = HttpRoutes.of[F] {
        case GET -> Root =>
          StaticFile.fromResource("/index.html").getOrElseF(NotFound())
        case _ =>
          NotFound()
      }
    } yield {
      Logger.httpApp(logHeaders = true, logBody = true)(
        Router(
          "/api/city" -> cityApiRoutes,
          "/api/employees" -> employeeRoutes,
          "/" -> (staticRoutes <+> fallbackRoute)
        ).orNotFound
      )
    }
  }
}