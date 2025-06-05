package publicationtracker.model.db

import java.util.UUID
import publicationtracker.model.ConferencesAndPublications.{ConferenceData, ConferenceDataF}
import cats.Id

case class DbConferenceData(
                             id: UUID,
                             conferenceId: UUID,
                             collectionId: UUID,
                             statusId: UUID
                           )

object DbConferenceData {
  def fromCore(core: ConferenceData): DbConferenceData =
    DbConferenceData(
      id = core.id,
      conferenceId = core.conferenceId,
      collectionId = core.collectionId,
      statusId = core.statusId
    )

  def toCore(db: DbConferenceData): ConferenceData =
    ConferenceDataF[Id](
      id = db.id,
      conferenceId = db.conferenceId,
      collectionId = db.collectionId,
      statusId = db.statusId
    )
}