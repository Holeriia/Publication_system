package publicationtracker.http.routes

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import io.circe.Encoder
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import publicationtracker.model.CoreEntities.Employee
import publicationtracker.model.CoreEntitiesCodecs.*
import publicationtracker.model.view.{EmployeeFull, EmployeeShortInfo, given}
import publicationtracker.service.EmployeeService


class EmployeeRoutes[F[_]: Async](service: EmployeeService[F]) extends Http4sDsl[F] {
  // Дополнительно: given для List[EmployeeShortInfo]
  given Encoder[List[EmployeeShortInfo]] = Encoder.encodeList(EmployeeShortInfo.given_Encoder_EmployeeShortInfo)
  given EntityEncoder[F, List[EmployeeShortInfo]] = jsonEncoderOf[F, List[EmployeeShortInfo]]

  // Прочие кодеки
  implicit val employeeDecoder: EntityDecoder[F, Employee] = jsonOf[F, Employee]
  implicit val employeeEncoder: EntityEncoder[F, Employee] = jsonEncoderOf[F, Employee]
  implicit val employeeFullEncoder: EntityEncoder[F, EmployeeFull] = jsonEncoderOf[F, EmployeeFull]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    // Краткая информация (только ФИО и id)
    case GET -> Root =>
      service.getAll.flatMap { employees =>
        val shortInfo = employees.map { e =>
          val patronymicStr = e.patronymic.getOrElse("")
          EmployeeShortInfo(
            e.id,
            s"${e.lastName} ${e.firstName} $patronymicStr".trim
          )
        }
        Ok(shortInfo)
      }

    // Полная информация (вся сущность Employee)
    case GET -> Root / UUIDVar(id) =>
      service.getById(id).flatMap {
        case Some(emp) => Ok(emp.asJson)
        case None      => NotFound()
      }

    // Создание нового сотрудника
    case req @ POST -> Root =>
      for {
        emp <- req.as[Employee]
        _   <- service.create(emp)
        res <- Created()
      } yield res

    // Обновление существующего сотрудника
    case req @ PUT -> Root / UUIDVar(id) =>
      for {
        emp <- req.as[Employee]
        _   <- service.update(emp.copy(id = id))
        res <- Ok()
      } yield res

    // Удаление сотрудника
    case DELETE -> Root / UUIDVar(id) =>
      service.delete(id).flatMap {
        case true  => NoContent()
        case false => NotFound()
      }

    // Расширенная информация (с привязками к достижениям и справочникам)
    case GET -> Root / UUIDVar(id) / "full" =>
      service.getFull(id).flatMap {
        case Some(full) => Ok(full.asJson)
        case None       => NotFound()
      }
  }
}