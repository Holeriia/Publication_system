package publicationtracker.model.db

import cats.Id

import java.util.UUID
import publicationtracker.model.ConferencesAndPublications.{PublicationData, PublicationDataF}

case class DbPublicationData(
                              id: UUID,
                              publicationId: UUID,
                              collectionId: UUID,
                              numberPages: Option[String],
                              link: Option[String]
                            )

object DbPublicationData {
  def fromCore(core: PublicationData): DbPublicationData =
    DbPublicationData(
      id = core.id,
      publicationId = core.publicationId,
      collectionId = core.collectionId,
      numberPages = core.numberPages,
      link = core.link
    )

  def toCore(db: DbPublicationData): PublicationData =
    PublicationDataF[Id](
      id = db.id,
      publicationId = db.publicationId,
      collectionId = db.collectionId,
      numberPages = db.numberPages,
      link = db.link
    )
}
