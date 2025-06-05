package publicationtracker.repository

import cats.Id
import cats.data.NonEmptyList
import publicationtracker.model.ReferenceData.ReferenceF
import java.util.UUID

trait ReferenceRepository[F[_]] {
  def getAll: F[List[ReferenceF[Id]]]
  def getById(id: UUID): F[Option[ReferenceF[Id]]]
  def insert(entity: ReferenceF[Id]): F[Unit]
  def insertMany(entities: NonEmptyList[ReferenceF[Id]]): F[Unit]
  def delete(id: UUID): F[Boolean]
}
