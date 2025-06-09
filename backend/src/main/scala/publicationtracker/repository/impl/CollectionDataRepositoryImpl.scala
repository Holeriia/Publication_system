package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Read, Write}
import fs2.Stream
import publicationtracker.model.ConferencesAndPublications.{CollectionData, CollectionDataF}
import publicationtracker.model.db.DbCollectionData
import publicationtracker.repository.CollectionDataRepository

import java.util.UUID

class CollectionDataRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends CollectionDataRepository[F] {

  private val tableName = "collection_data"

  implicit val getDb: Read[DbCollectionData] =
    Read[(UUID, String, UUID, Option[String], Option[String])].map {
      case (id, name, typeId, filePath, link) =>
        DbCollectionData(id, name, typeId, filePath, link)
    }

  implicit val putDb: Write[DbCollectionData] =
    Write[(UUID, String, UUID, Option[String], Option[String])].contramap { db =>
      (db.id, db.name, db.typeId, db.filePath, db.link)
    }

  override def getAll: F[List[CollectionDataF[Id]]] =
    (fr"SELECT id, name, type_id, file_path, link FROM " ++ Fragment.const(tableName))
      .query[DbCollectionData]
      .to[List]
      .transact(xa)
      .map(_.map(DbCollectionData.toCore))

  override def getById(id: UUID): F[Option[CollectionDataF[Id]]] =
    (fr"SELECT id, name, type_id, file_path, link FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbCollectionData]
      .option
      .transact(xa)
      .map(_.map(DbCollectionData.toCore))

  override def insert(entity: CollectionDataF[Id]): F[Unit] = {
    val db = DbCollectionData.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, name, type_id, file_path, link) VALUES (${db.id}, ${db.name}, ${db.typeId}, ${db.filePath}, ${db.link})")
      .update.run.transact(xa).void
  }

  override def update(entity: CollectionDataF[Id]): F[Unit] = {
    val db = DbCollectionData.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET name = ${db.name},
          type_id = ${db.typeId},
          file_path = ${db.filePath},
          link = ${db.link}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, CollectionData] =
    (fr"SELECT id, collection_type_id, collection_name FROM" ++ Fragment.const(tableName))
      .query[DbCollectionData]
      .stream
      .transact(xa)
      .map(DbCollectionData.toCore)
}
