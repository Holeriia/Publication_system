package publicationtracker.service.impl

import cats.effect.Async
import cats.syntax.all.*
import java.util.UUID

import publicationtracker.model.Achievements.Achievent
import publicationtracker.model.view.OtherAchievementView
import publicationtracker.repository.{AchieventRepository, ReferenceRepository}
import publicationtracker.service.AchieventService

class AchieventServiceImpl[F[_]: Async](
                                         achieventRepo: AchieventRepository[F],
                                         achievementTypeRepo: ReferenceRepository[F]
                                       ) extends AchieventService[F] {

  override def getAll: F[List[Achievent]] =
    achieventRepo.getAll

  override def getById(id: UUID): F[Option[Achievent]] =
    achieventRepo.getById(id)

  override def create(achievent: Achievent): F[Unit] =
    achieventRepo.insert(achievent)

  override def update(achievent: Achievent): F[Unit] =
    achieventRepo.update(achievent)

  override def delete(id: UUID): F[Boolean] =
    achieventRepo.delete(id)

  override def getOtherAchievementsByEmployee(employeeId: UUID): F[List[OtherAchievementView]] =
    for {
      maybeOtherType <- achievementTypeRepo.findByName("другое")
      otherTypeId <- maybeOtherType match {
        case Some(t) => Async[F].pure(t.id)
        case None => Async[F].raiseError(new Exception("Achievement type 'Other' not found"))
      }
      others <- achieventRepo.getOtherAchievementsByEmployee(employeeId, otherTypeId)
    } yield others
}