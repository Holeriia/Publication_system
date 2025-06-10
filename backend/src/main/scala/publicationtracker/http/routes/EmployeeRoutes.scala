package publicationtracker.http.routes

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import io.circe.{Encoder, Json}
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import publicationtracker.model.CoreEntities.Employee
import publicationtracker.model.CoreEntitiesCodecs.*
import publicationtracker.model.view.{EmployeeFull, EmployeeShortInfo, OtherAchievementView, given}
import publicationtracker.service.EmployeeService
import io.circe.generic.semiauto.deriveEncoder
import publicationtracker.model.ReferenceCodecs.given
import publicationtracker.model.view.EmployeeFullResponse
import java.util.UUID

class EmployeeRoutes[F[_]: Async](service: EmployeeService[F]) extends Http4sDsl[F] {
  
  given Encoder[List[EmployeeShortInfo]] = Encoder.encodeList(EmployeeShortInfo.given_Encoder_EmployeeShortInfo)
  given EntityEncoder[F, List[EmployeeShortInfo]] = jsonEncoderOf[F, List[EmployeeShortInfo]]
  
  implicit val employeeDecoder: EntityDecoder[F, Employee] = jsonOf[F, Employee]
  implicit val employeeEncoder: EntityEncoder[F, Employee] = jsonEncoderOf[F, Employee]
  implicit val employeeFullEncoder: Encoder[EmployeeFull] = deriveEncoder[EmployeeFull]
  implicit val otherAchievementViewEncoder: Encoder[OtherAchievementView] = deriveEncoder[OtherAchievementView]
  implicit val employeeFullResponseEncoder: Encoder[EmployeeFullResponse] = deriveEncoder[EmployeeFullResponse]
  implicit val employeeFullResponseEntityEncoder: EntityEncoder[F, EmployeeFullResponse] = jsonEncoderOf[F, EmployeeFullResponse]

  implicit val fullWithOthersEncoder: EntityEncoder[F, (EmployeeFull, List[OtherAchievementView])] =
    jsonEncoderOf[F, Json].contramap { case (empFull, others) =>
      Json.obj(
        "employee" -> empFull.asJson,
        "otherAchievements" -> others.asJson
      )
    }

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

    // Расширенная информация с достижениями "другое"
    case GET -> Root / UUIDVar(id) / "full" =>
      service.getFullWithOtherAchievements(id).flatMap {
        case Some(value) => Ok(EmployeeFullResponse(value._1, value._2))
        case None        => NotFound()
      }
  }
}