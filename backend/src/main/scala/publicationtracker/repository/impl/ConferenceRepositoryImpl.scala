package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Read, Write}
import fs2.Stream
import publicationtracker.model.ConferencesAndPublications.{Conference, ConferenceF}
import publicationtracker.model.db.DbConference
import publicationtracker.repository.ConferenceRepository

import java.time.LocalDate
import java.util.UUID

class ConferenceRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends ConferenceRepository[F] {

  private val tableName = "conference"

  // Убираем getDb/putDb — doobie автоматически сгенерирует

  override def getAll: F[List[ConferenceF[Id]]] =
    (fr"SELECT id, name, level_id, organisation_id, date, regulation_file, participants_count FROM " ++ Fragment.const(tableName))
      .query[DbConference]
      .to[List]
      .transact(xa)
      .map(_.map(DbConference.toCore))

  override def getById(id: UUID): F[Option[ConferenceF[Id]]] =
    (fr"SELECT id, name, level_id, organisation_id, date, regulation_file, participants_count FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbConference]
      .option
      .transact(xa)
      .map(_.map(DbConference.toCore))

  override def insert(entity: ConferenceF[Id]): F[Unit] = {
    val db = DbConference.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, name, level_id, organisation_id, date, regulation_file, participants_count) VALUES (${db.id}, ${db.name}, ${db.levelId}, ${db.organisationId}, ${db.date}, ${db.regulationFile}, ${db.participantsCount})")
      .update.run.transact(xa).void
  }

  override def update(entity: ConferenceF[Id]): F[Unit] = {
    val db = DbConference.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET name = ${db.name},
          level_id = ${db.levelId},
          organisation_id = ${db.organisationId},
          date = ${db.date},
          regulation_file = ${db.regulationFile},
          participants_count = ${db.participantsCount}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, Conference] =
    (fr"SELECT id, name, level_id, organisation_id, date, regulation_file, participants_count FROM " ++ Fragment.const(tableName))
      .query[DbConference]
      .stream
      .transact(xa)
      .map(DbConference.toCore)
}

