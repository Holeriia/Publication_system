package publicationtracker.repository

import cats.Id
import fs2.Stream
import publicationtracker.model.Achievements.AchieventF

import java.util.UUID

trait AchieventRepository[F[_]] {

  def getAll: F[List[AchieventF[Id]]]
  def getById(id: UUID): F[Option[AchieventF[Id]]]
  def insert(achievent: AchieventF[Id]): F[Unit]
  def update(achievent: AchieventF[Id]): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, AchieventF[Id]]
  def getByIds(ids: List[UUID]): F[List[AchieventF[Id]]]
}