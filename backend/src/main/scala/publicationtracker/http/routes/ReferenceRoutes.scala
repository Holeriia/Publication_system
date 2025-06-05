package publicationtracker.http.routes

import cats.Id
import cats.data.NonEmptyList
import cats.effect.Async
import cats.syntax.all.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import publicationtracker.model.ReferenceData.ReferenceF
import publicationtracker.repository.ReferenceRepository

import java.util.UUID

class ReferenceRoutes[F[_]: Async](name: String, repo: ReferenceRepository[F]) extends Http4sDsl[F] {

  implicit val decoder: EntityDecoder[F, ReferenceF[Id]] = jsonOf
  implicit val nelDecoder: EntityDecoder[F, NonEmptyList[ReferenceF[Id]]] = jsonOf

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / `name` =>
      repo.getAll.flatMap(data => Ok(data.asJson))

    case GET -> Root / `name` / UUIDVar(id) =>
      repo.getById(id).flatMap {
        case Some(ref) => Ok(ref.asJson)
        case None      => NotFound()
      }

    case req @ POST -> Root / `name` =>
      for {
        ref <- req.as[ReferenceF[Id]]
        _   <- repo.insert(ref)
        res <- Created()
      } yield res

    case req @ POST -> Root / `name` / "batch" =>
      for {
        refs <- req.as[NonEmptyList[ReferenceF[Id]]]
        _    <- repo.insertMany(refs)
        res  <- Created()
      } yield res

    case DELETE -> Root / `name` / UUIDVar(id) =>
      repo.delete(id).flatMap {
        case true  => NoContent()
        case false => NotFound()
      }
  }
}
