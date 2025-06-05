package publicationtracker.repository

import cats.Id
import publicationtracker.model.Achievements.AchievementAuthorF
import fs2.Stream

import java.util.UUID

trait AchievementAuthorRepository[F[_]] {
  def getAll: F[List[AchievementAuthorF[Id]]]
  def getById(id: UUID): F[Option[AchievementAuthorF[Id]]]
  def insert(entity: AchievementAuthorF[Id]): F[Unit]
  def update(entity: AchievementAuthorF[Id]): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, AchievementAuthorF[Id]]
}
