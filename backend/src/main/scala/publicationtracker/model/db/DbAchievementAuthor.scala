package publicationtracker.model.db

import cats.Id
import publicationtracker.model.Achievements.AchievementAuthorF

import java.util.UUID

case class DbAchievementAuthor(
                                id: UUID,
                                achieventId: UUID,
                                authorId: UUID,
                                publicationId: Option[UUID],
                                otherId: Option[UUID],
                                methodicalActivityId: Option[UUID],
                                professionalDevelopmentId: Option[UUID],
                                pattentsAndRegistrationId: Option[UUID],
                                authorOrder: Option[Int]
                              )

object DbAchievementAuthor {
  def fromCore(core: AchievementAuthorF[Id]): DbAchievementAuthor = DbAchievementAuthor(
    id = core.id,
    achieventId = core.achieventId,
    authorId = core.authorId,
    publicationId = core.publicationId,
    otherId = core.otherId,
    methodicalActivityId = core.methodicalActivityId,
    professionalDevelopmentId = core.professionalDevelopmentId,
    pattentsAndRegistrationId = core.pattentsAndRegistrationId,
    authorOrder = core.authorOrder
  )

  def toCore(db: DbAchievementAuthor): AchievementAuthorF[Id] = AchievementAuthorF(
    id = db.id,
    achieventId = db.achieventId,
    authorId = db.authorId,
    publicationId = db.publicationId,
    otherId = db.otherId,
    methodicalActivityId = db.methodicalActivityId,
    professionalDevelopmentId = db.professionalDevelopmentId,
    pattentsAndRegistrationId = db.pattentsAndRegistrationId,
    authorOrder = db.authorOrder
  )
}
