package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import publicationtracker.model.ConferencesAndPublications.ConferenceDataF
import publicationtracker.model.db.DbConferenceData
import publicationtracker.repository.ConferenceDataRepository
import fs2.Stream

import java.util.UUID

class ConferenceDataRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends ConferenceDataRepository[F] {

  private val tableName = "conference_data"

  implicit val getDbConferenceData: Read[DbConferenceData] =
    Read[(UUID, UUID, UUID, UUID)].map {
      case (id, conferenceId, collectionId, statusId) =>
        DbConferenceData(id, conferenceId, collectionId, statusId)
    }

  implicit val putDbConferenceData: Write[DbConferenceData] =
    Write[(UUID, UUID, UUID, UUID)].contramap { db =>
      (db.id, db.conferenceId, db.collectionId, db.statusId)
    }

  override def getAll: F[List[ConferenceDataF[cats.Id]]] =
    (fr"SELECT id, conference_id, collection_id, status_id FROM " ++ Fragment.const(tableName))
      .query[DbConferenceData]
      .to[List]
      .transact(xa)
      .map(_.map(DbConferenceData.toCore))

  override def getById(id: UUID): F[Option[ConferenceDataF[cats.Id]]] =
    (fr"SELECT id, conference_id, collection_id, status_id FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbConferenceData]
      .option
      .transact(xa)
      .map(_.map(DbConferenceData.toCore))

  override def insert(entity: ConferenceDataF[cats.Id]): F[Unit] = {
    val db = DbConferenceData.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, conference_id, collection_id, status_id) VALUES (${db.id}, ${db.conferenceId}, ${db.collectionId}, ${db.statusId})")
      .update.run.transact(xa).void
  }

  override def update(entity: ConferenceDataF[cats.Id]): F[Unit] = {
    val db = DbConferenceData.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET conference_id = ${db.conferenceId},
          collection_id = ${db.collectionId},
          status_id = ${db.statusId}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, ConferenceDataF[Id]] =
    (fr"SELECT id, conference_id, status_id FROM" ++ Fragment.const(tableName))
      .query[DbConferenceData]
      .stream
      .transact(xa)
      .map(DbConferenceData.toCore)  
}
