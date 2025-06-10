package publicationtracker.service

import java.util.UUID
import publicationtracker.model.Achievements.Achievent
import publicationtracker.model.view.OtherAchievementView

trait AchieventService[F[_]] {
  def getAll: F[List[Achievent]]
  def getById(id: UUID): F[Option[Achievent]]
  def create(achievent: Achievent): F[Unit]
  def update(achievent: Achievent): F[Unit]
  def delete(id: UUID): F[Boolean]

  def getOtherAchievementsByEmployee(employeeId: UUID): F[List[OtherAchievementView]]
}