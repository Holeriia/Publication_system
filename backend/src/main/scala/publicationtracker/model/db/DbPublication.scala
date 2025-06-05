package publicationtracker.model.db

import java.util.UUID
import java.time.LocalDate
import publicationtracker.model.ConferencesAndPublications.PublicationF
import cats.Id

case class DbPublication(
                          id: UUID,
                          name: String,
                          levelId: UUID,
                          typeId: UUID,
                          numberOfPages: Option[Int],
                          numberEz: Option[String],
                          numberEk: Option[String],
                          filePath: Option[String]
                        )

object DbPublication {
  def fromCore(core: PublicationF[Id]): DbPublication =
    DbPublication(
      id = core.id,
      name = core.name,
      levelId = core.levelId,
      typeId = core.typeId,
      numberOfPages = core.numberOfPages,
      numberEz = core.numberEz,
      numberEk = core.numberEk,
      filePath = core.filePath
    )

  def toCore(db: DbPublication): PublicationF[Id] =
    PublicationF[Id](
      id = db.id,
      name = db.name,
      levelId = db.levelId,
      typeId = db.typeId,
      numberOfPages = db.numberOfPages,
      numberEz = db.numberEz,
      numberEk = db.numberEk,
      filePath = db.filePath
    )
}
