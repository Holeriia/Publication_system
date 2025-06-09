package publicationtracker.repository

import cats.Id
import cats.effect.Async
import fs2.Stream
import publicationtracker.model.ConferencesAndPublications.PublicationDataF

import java.util.UUID

trait PublicationDataRepository[F[_]] {
  def getAll: F[List[PublicationDataF[cats.Id]]]
  def getById(id: UUID): F[Option[PublicationDataF[cats.Id]]]
  def insert(entity: PublicationDataF[cats.Id]): F[Unit]
  def update(entity: PublicationDataF[cats.Id]): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, PublicationDataF[Id]]
}
