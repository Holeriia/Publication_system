package publicationtracker

import cats.effect.*
import cats.syntax.semigroupk.*
import com.comcast.ip4s.{ipv4, port}
import doobie.hikari.HikariTransactor
import org.http4s.{HttpApp, HttpRoutes, StaticFile}
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.http4s.server.staticcontent.ResourceServiceBuilder
import publicationtracker.http.routes.ReferenceRoutes
import publicationtracker.repository.impl.CityRepository
import publicationtracker.util.Database
import publicationtracker.repository.impl.EmployeeRepositoryImpl
import publicationtracker.service.EmployeeService
import publicationtracker.http.routes.EmployeeRoutes
import publicationtracker.service.impl.EmployeeServiceImpl


object Main extends IOApp {

  def httpApp(transactor: HikariTransactor[IO]): HttpApp[IO] = {
    val cityRepo = new CityRepository[IO](transactor)
    val cityApiRoutes = new ReferenceRoutes[IO]("city", cityRepo).routes

    // Репозиторий, сервис и роуты для сотрудников
    val employeeRepo = new EmployeeRepositoryImpl[IO](transactor)
    val employeeService: EmployeeService[IO] = new EmployeeServiceImpl(employeeRepo)
    val employeeRoutes = new EmployeeRoutes[IO](employeeService).routes

    val staticRoutes = ResourceServiceBuilder[IO]("/").toRoutes

    val fallbackRoute: HttpRoutes[IO] = HttpRoutes.of[IO] {
      case GET -> _ =>
        StaticFile.fromResource("/index.html")(using Sync[IO])
          .getOrElseF(NotFound())
    }

    Logger.httpApp(logHeaders = true, logBody = true)(
      Router(
        "/" -> (staticRoutes <+> fallbackRoute),
        "/api/city" -> cityApiRoutes,
        "/api/employees" -> employeeRoutes
      ).orNotFound
    )
  }

  override def run(args: List[String]): IO[ExitCode] = {
    Database.transactor[IO].use { transactor =>
      EmberServerBuilder.default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp(transactor))
        .build
        .use { server =>
          IO.println(s"✅ Сервер запущен на http://localhost:8080/") *>
            IO.println(s"⚙️  Статические файлы обслуживаются из /public") *>
            IO.never
        }
    }
  }
}