package publicationtracker.model.view

import java.util.UUID
import io.circe.Encoder
import io.circe.generic.semiauto.*

import java.time.LocalDate

case class OtherAchievementView(
                                 achievementId: UUID,
                                 otherName: String,
                                 otherDescription: Option[String],
                                 comment: Option[String],
                                 date: Option[LocalDate],
                                 coAuthors: List[String]
                               )

object OtherAchievementView {
  given Encoder[OtherAchievementView] = deriveEncoder
}