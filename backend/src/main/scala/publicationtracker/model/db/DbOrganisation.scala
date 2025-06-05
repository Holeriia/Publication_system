package publicationtracker.model.db

import cats.Id
import publicationtracker.model.CoreEntities.{Organisation, OrganisationF}

import java.util.UUID

case class DbOrganisation(
                           id: UUID,
                           fullName: String,
                           shortName: String,
                           typeId: UUID,
                           cityId: UUID
                         )

object DbOrganisation {
  def fromCore(core: Organisation): DbOrganisation = DbOrganisation(
    id = core.id,
    fullName = core.fullName,
    shortName = core.shortName,
    typeId = core.typeId,
    cityId = core.cityId
  )

  def toCore(db: DbOrganisation): OrganisationF[Id] = OrganisationF(
    id = db.id,
    fullName = db.fullName,
    shortName = db.shortName,
    typeId = db.typeId,
    cityId = db.cityId
  )
}
