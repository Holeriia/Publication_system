package publicationtracker.repository

import publicationtracker.model.Achievements.*
import cats.Id
import java.util.UUID
import fs2.Stream

trait PattentsAndRegistrationRepository[F[_]] {
  def getAll: F[List[PattentsAndRegistrationF[Id]]]
  def getById(id: UUID): F[Option[PattentsAndRegistrationF[Id]]]
  def insert(entity: PattentsAndRegistrationF[Id]): F[Unit]
  def update(entity: PattentsAndRegistrationF[Id]): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, PattentsAndRegistrationF[Id]]
}
