package publicationtracker.model.db

import cats.Id
import publicationtracker.model.Achievements.AchieventF

import java.time.LocalDate
import java.util.UUID

case class DbAchievent(
                        id: UUID,
                        typeId: UUID,

                        publicationId: Option[UUID],
                        otherId: Option[UUID],
                        methodicalActivityId: Option[UUID],
                        professionalDevelopmentId: Option[UUID],
                        pattentsAndRegistrationId: Option[UUID],

                        comment: Option[String],
                        rewardFile: Option[String],
                        date: Option[LocalDate]
                      )

object DbAchievent {

  def fromCore(core: AchieventF[Id]): DbAchievent =
    DbAchievent(
      id = core.id,
      typeId = core.typeId,

      publicationId = core.publicationId,
      otherId = core.otherId,
      methodicalActivityId = core.methodicalActivityId,
      professionalDevelopmentId = core.professionalDevelopmentId,
      pattentsAndRegistrationId = core.pattentsAndRegistrationId,

      comment = core.comment,
      rewardFile = core.rewardFile,
      date = core.date
    )

  def toCore(db: DbAchievent): AchieventF[Id] =
    AchieventF[Id](
      id = db.id,
      typeId = db.typeId,

      publicationId = db.publicationId,
      otherId = db.otherId,
      methodicalActivityId = db.methodicalActivityId,
      professionalDevelopmentId = db.professionalDevelopmentId,
      pattentsAndRegistrationId = db.pattentsAndRegistrationId,

      comment = db.comment,
      rewardFile = db.rewardFile,
      date = db.date
    )
}