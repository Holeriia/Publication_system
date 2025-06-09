package publicationtracker.repository

import fs2.Stream
import publicationtracker.model.CoreEntities.Author

import java.util.UUID

trait AuthorRepository[F[_]] {
  def getAll: F[List[Author]]
  def getById(id: UUID): F[Option[Author]]
  def insert(author: Author): F[Unit]
  def update(author: Author): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, Author]
  def getByEmployeeId(employeeId: UUID): F[List[Author]]
  def getByIds(ids: List[UUID]): F[List[Author]]
}
