package publicationtracker.model.db

import cats.Id
import publicationtracker.model.Achievements.*
import publicationtracker.model.Reference

import java.util.UUID

case class DbReference(id: UUID, name: String)

object Conversions {
  def toCore(r: Reference[Id]): DbReference =
    DbReference(r.id, r.name)

  def fromCore(d: DbReference): Reference[Id] =
    Reference[Id](d.id, d.name)
}
