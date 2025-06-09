package publicationtracker.repository

import fs2.Stream
import publicationtracker.model.ConferencesAndPublications.{CollectionData, CollectionDataF}

import java.util.UUID

trait CollectionDataRepository[F[_]] {
  def getAll: F[List[CollectionData]]
  def getById(id: UUID): F[Option[CollectionData]]
  def insert(entity: CollectionData): F[Unit]
  def update(entity: CollectionData): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, CollectionData]
}
