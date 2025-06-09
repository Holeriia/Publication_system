package publicationtracker.model.db

import cats.Id
import publicationtracker.model.Achievements.AchievementAuthorF

import java.util.UUID

case class DbAchievementAuthor(
                                id: UUID,
                                achieventId: UUID,
                                authorId: UUID,
                                authorOrder: Option[Int]
                              )

object DbAchievementAuthor {
  def fromCore(core: AchievementAuthorF[Id]): DbAchievementAuthor = DbAchievementAuthor(
    id = core.id,
    achieventId = core.achieventId,
    authorId = core.authorId,
    authorOrder = core.authorOrder
  )

  def toCore(db: DbAchievementAuthor): AchievementAuthorF[Id] = AchievementAuthorF(
    id = db.id,
    achieventId = db.achieventId,
    authorId = db.authorId,
    authorOrder = db.authorOrder
  )
}