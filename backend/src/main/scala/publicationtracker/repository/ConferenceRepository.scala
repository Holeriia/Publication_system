package publicationtracker.repository

import publicationtracker.model.ConferencesAndPublications.{Conference, ConferenceF}
import fs2.Stream
import java.util.UUID

trait ConferenceRepository[F[_]] {
  def getAll: F[List[Conference]]
  def getById(id: UUID): F[Option[Conference]]
  def insert(entity: Conference): F[Unit]
  def update(entity: Conference): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, Conference]
}
