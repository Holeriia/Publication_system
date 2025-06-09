package publicationtracker

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import org.http4s.ember.server.EmberServerBuilder
import publicationtracker.http.HttpServer
import publicationtracker.util.Database

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    Database.transactor[IO].use { transactor =>
      HttpServer.httpApp[IO](transactor).flatMap { httpApp =>
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpApp) // Передаем HttpApp
          .build
          .use { _ =>
            IO.println("✅ Сервер запущен на http://localhost:8080/") *>
              IO.println("⚙️  Статические файлы обслуживаются из /public") *>
              IO.never
          }
      }
    }
  }
}
