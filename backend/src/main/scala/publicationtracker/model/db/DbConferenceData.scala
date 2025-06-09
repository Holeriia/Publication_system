package publicationtracker.model.db

import cats.Id
import publicationtracker.model.ConferencesAndPublications.{ConferenceData, ConferenceDataF}

import java.util.UUID

case class DbConferenceData(
                             id: UUID,
                             conferenceId: UUID,
                             collectionId: UUID,
                             statusId: UUID,
                             participationFormatId: UUID // <--- добавлено
                           )

object DbConferenceData {
  def fromCore(core: ConferenceData): DbConferenceData =
    DbConferenceData(
      id = core.id,
      conferenceId = core.conferenceId,
      collectionId = core.collectionId,
      statusId = core.statusId,
      participationFormatId = core.participationFormatId // <--- добавлено
    )

  def toCore(db: DbConferenceData): ConferenceData =
    ConferenceDataF[Id](
      id = db.id,
      conferenceId = db.conferenceId,
      collectionId = db.collectionId,
      statusId = db.statusId,
      participationFormatId = db.participationFormatId // <--- добавлено
    )
}