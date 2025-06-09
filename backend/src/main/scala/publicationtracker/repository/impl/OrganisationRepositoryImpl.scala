package publicationtracker.repository.impl

import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import fs2.Stream
import publicationtracker.model.CoreEntities.Organisation
import publicationtracker.model.db.DbOrganisation
import publicationtracker.repository.OrganisationRepository

import java.util.UUID

class OrganisationRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends OrganisationRepository[F] {

  private val tableName = "organisation"

  implicit val getDbOrganisation: Read[DbOrganisation] =
    Read[(UUID, String, String, UUID, UUID)].map {
      case (id, fullName, shortName, typeId, cityId) =>
        DbOrganisation(id, fullName, shortName, typeId, cityId)
    }

  implicit val putDbOrganisation: Write[DbOrganisation] =
    Write[(UUID, String, String, UUID, UUID)].contramap { dbOrg =>
      (dbOrg.id, dbOrg.fullName, dbOrg.shortName, dbOrg.typeId, dbOrg.cityId)
    }

  override def getAll: F[List[Organisation]] =
    (fr"SELECT id, full_name, short_name, type_id, city_id FROM " ++ Fragment.const(tableName))
      .query[DbOrganisation]
      .to[List]
      .transact(xa)
      .map(_.map(DbOrganisation.toCore))

  override def getById(id: UUID): F[Option[Organisation]] =
    (fr"SELECT id, full_name, short_name, type_id, city_id FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbOrganisation]
      .option
      .transact(xa)
      .map(_.map(DbOrganisation.toCore))

  override def insert(org: Organisation): F[Unit] = {
    val dbOrg = DbOrganisation.fromCore(org)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, full_name, short_name, type_id, city_id) VALUES (${dbOrg.id}, ${dbOrg.fullName}, ${dbOrg.shortName}, ${dbOrg.typeId}, ${dbOrg.cityId})")
      .update.run.transact(xa).void
  }

  override def update(org: Organisation): F[Unit] = {
    val dbOrg = DbOrganisation.fromCore(org)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET full_name = ${dbOrg.fullName},
          short_name = ${dbOrg.shortName},
          type_id = ${dbOrg.typeId},
          city_id = ${dbOrg.cityId}
      WHERE id = ${dbOrg.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, Organisation] =
    (fr"SELECT id, full_name, short_name, type_id, city_id FROM " ++ Fragment.const(tableName))
      .query[DbOrganisation]
      .stream
      .transact(xa)
      .map(DbOrganisation.toCore)
}
