package publicationtracker.model.db

import cats.Id
import publicationtracker.model.CoreEntities.EmployeeF

import java.time.LocalDate
import java.util.UUID

case class DbEmployee(
                       id: UUID,
                       firstName: String,
                       lastName: String,
                       patronymic: Option[String],
                       degreeId: Option[UUID],
                       titleId: Option[UUID],
                       postId: Option[UUID],
                       universityStartDate: Option[LocalDate],
                       industryStartDate: Option[LocalDate],
                       experienceComment: Option[String],
                       diplomaEducation: Option[String]
                     )

object DbEmployee {
  def fromCore(core: EmployeeF[Id]): DbEmployee =
    DbEmployee(
      id = core.id,
      firstName = core.firstName,
      lastName = core.lastName,
      patronymic = core.patronymic,
      degreeId = core.degreeId,
      titleId = core.titleId,
      postId = core.postId,
      universityStartDate = core.universityStartDate,
      industryStartDate = core.industryStartDate,
      experienceComment = core.experienceComment,
      diplomaEducation = core.diplomaEducation
    )

  def toCore(db: DbEmployee): EmployeeF[Id] =
    EmployeeF(
      id = db.id,
      firstName = db.firstName,
      lastName = db.lastName,
      patronymic = db.patronymic,
      degreeId = db.degreeId,
      titleId = db.titleId,
      postId = db.postId,
      universityStartDate = db.universityStartDate,
      industryStartDate = db.industryStartDate,
      experienceComment = db.experienceComment,
      diplomaEducation = db.diplomaEducation
    )
}
