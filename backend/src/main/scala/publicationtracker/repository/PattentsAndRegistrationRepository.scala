package publicationtracker.repository

import cats.Id
import fs2.Stream
import publicationtracker.model.Achievements.*

import java.util.UUID

trait PattentsAndRegistrationRepository[F[_]] {
  def getAll: F[List[PattentsAndRegistrationF[Id]]]
  def getById(id: UUID): F[Option[PattentsAndRegistrationF[Id]]]
  def insert(entity: PattentsAndRegistrationF[Id]): F[Unit]
  def update(entity: PattentsAndRegistrationF[Id]): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, PattentsAndRegistrationF[Id]]
}
