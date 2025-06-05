package publicationtracker.model.db

import publicationtracker.model.ConferencesAndPublications.CollectionDataF
import cats.Id

import java.util.UUID

case class DbCollectionData(
                             id: UUID,
                             name: String,
                             typeId: UUID,
                             filePath: Option[String],
                             link: Option[String]
                           )

object DbCollectionData {
  def fromCore(c: CollectionDataF[Id]): DbCollectionData =
    DbCollectionData(
      c.id,
      c.name,
      c.typeId,
      c.filePath,
      c.link
    )

  def toCore(db: DbCollectionData): CollectionDataF[Id] =
    CollectionDataF[Id](
      id = db.id,
      name = db.name,
      typeId = db.typeId,
      filePath = db.filePath,
      link = db.link
    )
}
