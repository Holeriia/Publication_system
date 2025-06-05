package publicationtracker.model.db

import cats.Id
import publicationtracker.model.Achievements.AchieventF
import publicationtracker.model.CoreEntities.*

import java.time.LocalDate
import java.util.UUID

case class DbAchievent(
                        id: UUID,
                        typeId: UUID,
                        rewardFile: Option[String],
                        date: Option[LocalDate]
                      )

object DbAchievent {

  def fromCore(core: AchieventF[Id]): DbAchievent = DbAchievent(
    id = core.id,
    typeId = core.typeId,
    rewardFile = core.rewardFile,
    date = core.date
  )

  def toCore(db: DbAchievent): AchieventF[Id] = AchieventF(
    id = db.id,
    typeId = db.typeId,
    rewardFile = db.rewardFile,
    date = db.date
  )
}
