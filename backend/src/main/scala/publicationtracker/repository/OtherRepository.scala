package publicationtracker.repository

import cats.Id
import cats.effect.Async
import publicationtracker.model.Achievements.OtherF
import fs2.Stream
import java.util.UUID

trait OtherRepository[F[_]] {
  def getAll: F[List[OtherF[Id]]]
  def getById(id: UUID): F[Option[OtherF[Id]]]
  def insert(entity: OtherF[Id]): F[Unit]
  def update(entity: OtherF[Id]): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, OtherF[Id]]
}
