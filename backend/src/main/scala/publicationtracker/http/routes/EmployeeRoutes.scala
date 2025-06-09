package publicationtracker.http.routes

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import publicationtracker.model.CoreEntities.Employee
import publicationtracker.model.ReferenceCodecs.given
import publicationtracker.model.view.EmployeeFull
import publicationtracker.service.EmployeeService
import publicationtracker.model.ReferenceCodecs.given
import publicationtracker.model.view.given
import publicationtracker.model.CoreEntitiesCodecs.given

import java.util.UUID

class EmployeeRoutes[F[_]: Async](service: EmployeeService[F]) extends Http4sDsl[F] {

  implicit val decoder: EntityDecoder[F, Employee] = jsonOf[F, Employee]
  implicit val listEncoder: EntityEncoder[F, List[Employee]] = jsonEncoderOf[F, List[Employee]]
  

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root =>
      service.getAll.flatMap(data => Ok(data.asJson))

    case GET -> Root / UUIDVar(id) =>
      service.getById(id).flatMap {
        case Some(emp) => Ok(emp.asJson)
        case None      => NotFound()
      }

    case req @ POST -> Root =>
      for {
        emp <- req.as[Employee]
        _   <- service.create(emp)
        res <- Created()
      } yield res

    case req @ PUT -> Root / UUIDVar(id) =>
      for {
        emp <- req.as[Employee]
        _   <- service.update(emp.copy(id = id))
        res <- Ok()
      } yield res

    case DELETE -> Root / UUIDVar(id) =>
      service.delete(id).flatMap {
        case true  => NoContent()
        case false => NotFound()
      }

    case GET -> Root / UUIDVar(id) / "full" =>
      service.getFull(id).flatMap {
        case Some(full) => Ok(full.asJson)
        case None       => NotFound()
      }
  }
}