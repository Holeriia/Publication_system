package publicationtracker.repository

import fs2.Stream
import publicationtracker.model.Achievements.MethodicalActivity

import java.util.UUID

trait MethodicalActivityRepository[F[_]] {
  def getAll: F[List[MethodicalActivity]]
  def getById(id: UUID): F[Option[MethodicalActivity]]
  def insert(methodicalActivity: MethodicalActivity): F[Unit]
  def update(methodicalActivity: MethodicalActivity): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, MethodicalActivity]
}
