package publicationtracker.repository

import publicationtracker.model.Achievements.ProfessionalDevelopment
import java.util.UUID
import fs2.Stream

trait ProfessionalDevelopmentRepository[F[_]] {
  def getAll: F[List[ProfessionalDevelopment]]
  def getById(id: UUID): F[Option[ProfessionalDevelopment]]
  def insert(entity: ProfessionalDevelopment): F[Unit]
  def update(entity: ProfessionalDevelopment): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, ProfessionalDevelopment]
}
