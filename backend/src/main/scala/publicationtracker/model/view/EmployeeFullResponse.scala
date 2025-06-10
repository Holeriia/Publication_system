package publicationtracker.model.view

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class EmployeeFullResponse(
                                 employee: EmployeeFull,
                                 otherAchievements: List[OtherAchievementView],
                                 // тут другие типы достижений
                               )
object EmployeeFullResponse {
  given Encoder[EmployeeFullResponse] = deriveEncoder
}