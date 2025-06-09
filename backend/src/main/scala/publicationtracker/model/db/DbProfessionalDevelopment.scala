package publicationtracker.model.db

import cats.Id
import publicationtracker.model.Achievements.*

import java.time.LocalDate
import java.util.UUID

case class DbProfessionalDevelopment(
                                      id: UUID,
                                      name: String,
                                      dateStart: Option[LocalDate],
                                      dateEnd: Option[LocalDate],
                                      organisationId: UUID
                                    )

object DbProfessionalDevelopment {
  def fromCore(core: ProfessionalDevelopment): DbProfessionalDevelopment =
    DbProfessionalDevelopment(
      id = core.id,
      name = core.name,
      dateStart = core.dateStart,
      dateEnd = core.dateEnd,
      organisationId = core.organisationId
    )

  def toCore(db: DbProfessionalDevelopment): ProfessionalDevelopment =
    ProfessionalDevelopmentF(
      id = db.id,
      name = db.name,
      dateStart = db.dateStart,
      dateEnd = db.dateEnd,
      organisationId = db.organisationId
    )
}
