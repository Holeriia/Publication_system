package publicationtracker.model.db

import publicationtracker.model.Achievements.OtherF
import cats.Id
import java.util.UUID

case class DbOther(
                    id: UUID,
                    name: String,
                    description: Option[String],
                    filePath: Option[String]
                  )

object DbOther {
  def fromCore(core: OtherF[Id]): DbOther = DbOther(
    id = core.id,
    name = core.name,
    description = core.description,
    filePath = core.filePath
  )

  def toCore(db: DbOther): OtherF[Id] = OtherF(
    id = db.id,
    name = db.name,
    description = db.description,
    filePath = db.filePath
  )
}
