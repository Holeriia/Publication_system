package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import fs2.Stream
import publicationtracker.model.Achievements.PattentsAndRegistrationF
import publicationtracker.model.db.DbPattentsAndRegistration
import publicationtracker.repository.PattentsAndRegistrationRepository

import java.time.LocalDate
import java.util.UUID

class PattentsAndRegistrationRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends PattentsAndRegistrationRepository[F] {

  private val tableName = "pattents_and_registration"

  implicit val getDb: Read[DbPattentsAndRegistration] =
    Read[(UUID, String, Option[String], Option[LocalDate], Option[String])].map {
      case (id, name, number, regDate, note) =>
        DbPattentsAndRegistration(id, name, number, regDate, note)
    }

  implicit val putDb: Write[DbPattentsAndRegistration] =
    Write[(UUID, String, Option[String], Option[LocalDate], Option[String])].contramap { db =>
      (db.id, db.name, db.number, db.registrationDate, db.note)
    }

  override def getAll: F[List[PattentsAndRegistrationF[Id]]] =
    (fr"SELECT id, name, number, registration_date, note FROM " ++ Fragment.const(tableName))
      .query[DbPattentsAndRegistration]
      .to[List]
      .transact(xa)
      .map(_.map(DbPattentsAndRegistration.toCore))

  override def getById(id: UUID): F[Option[PattentsAndRegistrationF[Id]]] =
    (fr"SELECT id, name, number, registration_date, note FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbPattentsAndRegistration]
      .option
      .transact(xa)
      .map(_.map(DbPattentsAndRegistration.toCore))

  override def insert(entity: PattentsAndRegistrationF[Id]): F[Unit] = {
    val db = DbPattentsAndRegistration.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++ fr"""
      (id, name, number, registration_date, note)
      VALUES (${db.id}, ${db.name}, ${db.number}, ${db.registrationDate}, ${db.note})
    """).update.run.transact(xa).void
  }

  override def update(entity: PattentsAndRegistrationF[Id]): F[Unit] = {
    val db = DbPattentsAndRegistration.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET name = ${db.name},
          number = ${db.number},
          registration_date = ${db.registrationDate},
          note = ${db.note}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, PattentsAndRegistrationF[Id]] =
    (fr"SELECT id, employee_id, registration_date, registration_number FROM" ++ Fragment.const(tableName))
      .query[DbPattentsAndRegistration]
      .stream
      .transact(xa)
      .map(DbPattentsAndRegistration.toCore)
}
