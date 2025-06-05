package publicationtracker.repository

import publicationtracker.model.CoreEntities.Author

import java.util.UUID
import fs2.Stream

trait AuthorRepository[F[_]] {
  def getAll: F[List[Author]]
  def getById(id: UUID): F[Option[Author]]
  def insert(author: Author): F[Unit]
  def update(author: Author): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, Author]
}
