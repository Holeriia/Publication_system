package publicationtracker.model.db

import cats.Id
import publicationtracker.model.Achievements.*

import java.time.LocalDate
import java.util.UUID

case class DbPattentsAndRegistration(
                                      id: UUID,
                                      name: String,
                                      number: Option[String],
                                      registrationDate: Option[LocalDate],
                                      note: Option[String]
                                    )

object DbPattentsAndRegistration {

  def fromCore(core: PattentsAndRegistration): DbPattentsAndRegistration =
    DbPattentsAndRegistration(
      id = core.id,
      name = core.name,
      number = core.number,
      registrationDate = core.registrationDate,
      note = core.note
    )

  def toCore(db: DbPattentsAndRegistration): PattentsAndRegistration =
    PattentsAndRegistrationF[Id](
      id = db.id,
      name = db.name,
      number = db.number,
      registrationDate = db.registrationDate,
      note = db.note
    )
}
