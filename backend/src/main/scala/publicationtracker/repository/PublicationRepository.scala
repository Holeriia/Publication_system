package publicationtracker.repository

import cats.Id
import cats.effect.Async
import fs2.Stream
import publicationtracker.model.ConferencesAndPublications.PublicationF

import java.util.UUID

trait PublicationRepository[F[_]] {
  def getAll: F[List[PublicationF[Id]]]
  def getById(id: UUID): F[Option[PublicationF[Id]]]
  def insert(entity: PublicationF[Id]): F[Unit]
  def update(entity: PublicationF[Id]): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, PublicationF[Id]]
}