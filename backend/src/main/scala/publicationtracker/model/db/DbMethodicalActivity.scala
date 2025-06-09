package publicationtracker.model.db

import cats.Id
import publicationtracker.model.Achievements.*

import java.util.UUID

case class DbMethodicalActivity(
                                 id: UUID,
                                 name: String,
                                 typeId: UUID,
                                 numberOfPages: Option[Int],
                                 filePath: Option[String]
                               )

object DbMethodicalActivity {
  def fromCore(core: MethodicalActivity): DbMethodicalActivity =
    DbMethodicalActivity(
      id = core.id,
      name = core.name,
      typeId = core.typeId,
      numberOfPages = core.numberOfPages,
      filePath = core.filePath
    )

  def toCore(db: DbMethodicalActivity): MethodicalActivity =
    MethodicalActivityF[Id](
      id = db.id,
      name = db.name,
      typeId = db.typeId,
      numberOfPages = db.numberOfPages,
      filePath = db.filePath
    )
}
