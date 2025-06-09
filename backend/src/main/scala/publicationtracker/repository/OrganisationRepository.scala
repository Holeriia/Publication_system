package publicationtracker.repository

import fs2.Stream
import publicationtracker.model.CoreEntities.Organisation

import java.util.UUID

trait OrganisationRepository[F[_]] {
  def getAll: F[List[Organisation]]
  def getById(id: UUID): F[Option[Organisation]]
  def insert(org: Organisation): F[Unit]
  def update(org: Organisation): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, Organisation]
}
