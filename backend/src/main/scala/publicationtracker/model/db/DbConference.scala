package publicationtracker.model.db

import cats.Id
import publicationtracker.model.ConferencesAndPublications.ConferenceF

import java.time.LocalDate
import java.util.UUID

case class DbConference(
                         id: UUID,
                         name: String,
                         levelId: UUID,
                         organisationId: UUID,
                         date: Option[LocalDate],
                         regulationFile: Option[String],
                         participantsCount: Option[Int] // новое поле
                       )

object DbConference {
  def fromCore(c: ConferenceF[Id]): DbConference =
    DbConference(
      c.id,
      c.name,
      c.levelId,
      c.organisationId,
      c.date,
      c.regulationFile,
      c.participantsCount // добавлено
    )

  def toCore(db: DbConference): ConferenceF[Id] =
    ConferenceF[Id](
      id = db.id,
      name = db.name,
      levelId = db.levelId,
      organisationId = db.organisationId,
      date = db.date,
      regulationFile = db.regulationFile,
      participantsCount = db.participantsCount // добавлено
    )
}

