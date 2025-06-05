package publicationtracker.repository.impl

import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import publicationtracker.model.Achievements.ProfessionalDevelopment
import publicationtracker.model.db.DbProfessionalDevelopment
import publicationtracker.repository.ProfessionalDevelopmentRepository
import fs2.Stream
import java.util.UUID
import java.time.LocalDate

class ProfessionalDevelopmentRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends ProfessionalDevelopmentRepository[F] {

  private val tableName = "professional_development"

  implicit val getDb: Read[DbProfessionalDevelopment] =
    Read[(UUID, String, Option[LocalDate], Option[LocalDate], UUID)].map {
      case (id, name, start, end, orgId) =>
        DbProfessionalDevelopment(id, name, start, end, orgId)
    }

  implicit val putDb: Write[DbProfessionalDevelopment] =
    Write[(UUID, String, Option[LocalDate], Option[LocalDate], UUID)].contramap { db =>
      (db.id, db.name, db.dateStart, db.dateEnd, db.organisationId)
    }

  override def getAll: F[List[ProfessionalDevelopment]] =
    (fr"SELECT id, name, date_start, date_end, organisation_id FROM " ++ Fragment.const(tableName))
      .query[DbProfessionalDevelopment]
      .to[List]
      .transact(xa)
      .map(_.map(DbProfessionalDevelopment.toCore))

  override def getById(id: UUID): F[Option[ProfessionalDevelopment]] =
    (fr"SELECT id, name, date_start, date_end, organisation_id FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbProfessionalDevelopment]
      .option
      .transact(xa)
      .map(_.map(DbProfessionalDevelopment.toCore))

  override def insert(entity: ProfessionalDevelopment): F[Unit] = {
    val db = DbProfessionalDevelopment.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, name, date_start, date_end, organisation_id) VALUES (${db.id}, ${db.name}, ${db.dateStart}, ${db.dateEnd}, ${db.organisationId})")
      .update.run.transact(xa).void
  }

  override def update(entity: ProfessionalDevelopment): F[Unit] = {
    val db = DbProfessionalDevelopment.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET name = ${db.name},
          date_start = ${db.dateStart},
          date_end = ${db.dateEnd},
          organisation_id = ${db.organisationId}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, ProfessionalDevelopment] =
    (fr"SELECT id, employee_id, course_name, date_completed FROM" ++ Fragment.const(tableName))
      .query[DbProfessionalDevelopment]
      .stream
      .transact(xa)
      .map(DbProfessionalDevelopment.toCore)  
}
