package publicationtracker.repository

import cats.Id
import cats.data.NonEmptyList
import fs2.Stream
import publicationtracker.model.Achievements.{AchievementAuthor, AchievementAuthorF}

import java.util.UUID

trait AchievementAuthorRepository[F[_]] {
  def getAll: F[List[AchievementAuthor]]
  def getById(id: UUID): F[Option[AchievementAuthor]]
  def insert(entity: AchievementAuthor): F[Unit]
  def update(entity: AchievementAuthor): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, AchievementAuthor]
  def getByAuthorIds(authorIds: NonEmptyList[UUID]): F[List[AchievementAuthor]]
}