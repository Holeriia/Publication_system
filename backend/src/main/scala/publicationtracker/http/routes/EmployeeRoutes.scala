package publicationtracker.http.routes

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import publicationtracker.model.CoreEntities.Employee
import publicationtracker.service.EmployeeService

import java.util.UUID

class EmployeeRoutes[F[_]: Async](service: EmployeeService[F]) extends Http4sDsl[F] {

  // JSON сериализация/десериализация
  implicit val decoder: EntityDecoder[F, Employee] = jsonOf
  implicit val listEncoder: EntityEncoder[F, List[Employee]] = jsonEncoderOf

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "employees" =>
      service.getAll.flatMap(data => Ok(data.asJson))

    case GET -> Root / "employees" / UUIDVar(id) =>
      service.getById(id).flatMap {
        case Some(emp) => Ok(emp.asJson)
        case None      => NotFound()
      }

    case req @ POST -> Root / "employees" =>
      for {
        emp <- req.as[Employee]
        _   <- service.create(emp)
        res <- Created()
      } yield res

    case req @ PUT -> Root / "employees" / UUIDVar(id) =>
      for {
        emp <- req.as[Employee]
        _   <- service.update(emp.copy(id = id)) // переопределяем id
        res <- Ok()
      } yield res

    case DELETE -> Root / "employees" / UUIDVar(id) =>
      service.delete(id).flatMap {
        case true  => NoContent()
        case false => NotFound()
      }
  }
}
