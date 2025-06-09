package publicationtracker.repository

import cats.Id
import cats.effect.Async
import fs2.Stream
import publicationtracker.model.ConferencesAndPublications.ConferenceDataF

import java.util.UUID

trait ConferenceDataRepository[F[_]] {
  def getAll: F[List[ConferenceDataF[cats.Id]]]
  def getById(id: UUID): F[Option[ConferenceDataF[cats.Id]]]
  def insert(entity: ConferenceDataF[cats.Id]): F[Unit]
  def update(entity: ConferenceDataF[cats.Id]): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, ConferenceDataF[Id]]
}
