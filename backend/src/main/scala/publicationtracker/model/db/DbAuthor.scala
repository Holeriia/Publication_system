package publicationtracker.model.db

import cats.Id
import publicationtracker.model.CoreEntities.AuthorF

import java.util.UUID

case class DbAuthor(
                     id: UUID,
                     firstName: String,
                     lastName: String,
                     patronymic: Option[String],
                     isEmployee: Boolean,
                     employeeId: Option[UUID],
                     affiliation: Option[String]
                   )

object DbAuthor {
  def fromCore(core: AuthorF[Id]): DbAuthor = DbAuthor(
    id = core.id,
    firstName = core.firstName,
    lastName = core.lastName,
    patronymic = core.patronymic,
    isEmployee = core.isEmployee,
    employeeId = core.employeeId,
    affiliation = core.affiliation
  )

  def toCore(db: DbAuthor): AuthorF[Id] = AuthorF(
    id = db.id,
    firstName = db.firstName,
    lastName = db.lastName,
    patronymic = db.patronymic,
    isEmployee = db.isEmployee,
    employeeId = db.employeeId,
    affiliation = db.affiliation
  )
}
