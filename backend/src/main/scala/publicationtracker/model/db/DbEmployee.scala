package publicationtracker.model.db

import cats.Id
import publicationtracker.model.CoreEntities.EmployeeF

import java.util.UUID

case class DbEmployee(
                       id: UUID,
                       firstName: String,
                       lastName: String,
                       patronymic: Option[String],
                       degreeId: UUID,
                       titleId: UUID,
                       postId: UUID,
                       exp: Option[Int],
                       seniority: Option[Int],
                       diplomaEducation: Option[String]
                     )

object DbEmployee {
  def fromCore(core: EmployeeF[Id]): DbEmployee = DbEmployee(
    id = core.id,
    firstName = core.firstName,
    lastName = core.lastName,
    patronymic = core.patronymic,
    degreeId = core.degreeId,
    titleId = core.titleId,
    postId = core.postId,
    exp = core.exp,
    seniority = core.seniority,
    diplomaEducation = core.diplomaEducation
  )

  def toCore(db: DbEmployee): EmployeeF[Id] = EmployeeF(
    id = db.id,
    firstName = db.firstName,
    lastName = db.lastName,
    patronymic = db.patronymic,
    degreeId = db.degreeId,
    titleId = db.titleId,
    postId = db.postId,
    exp = db.exp,
    seniority = db.seniority,
    diplomaEducation = db.diplomaEducation
  )
}
