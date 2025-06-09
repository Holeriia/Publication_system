package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import fs2.Stream
import publicationtracker.model.ConferencesAndPublications.PublicationDataF
import publicationtracker.model.db.DbPublicationData
import publicationtracker.repository.PublicationDataRepository

import java.util.UUID

class PublicationDataRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends PublicationDataRepository[F] {

  private val tableName = "publication_data"

  implicit val getDbPublicationData: Read[DbPublicationData] =
    Read[(UUID, UUID, UUID, Option[String], Option[String])].map {
      case (id, publicationId, collectionId, numberPages, link) =>
        DbPublicationData(id, publicationId, collectionId, numberPages, link)
    }

  implicit val putDbPublicationData: Write[DbPublicationData] =
    Write[(UUID, UUID, UUID, Option[String], Option[String])].contramap { db =>
      (db.id, db.publicationId, db.collectionId, db.numberPages, db.link)
    }

  override def getAll: F[List[PublicationDataF[cats.Id]]] =
    (fr"SELECT id, publication_id, collection_id, number_pages, link FROM " ++ Fragment.const(tableName))
      .query[DbPublicationData]
      .to[List]
      .transact(xa)
      .map(_.map(DbPublicationData.toCore))

  override def getById(id: UUID): F[Option[PublicationDataF[cats.Id]]] =
    (fr"SELECT id, publication_id, collection_id, number_pages, link FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbPublicationData]
      .option
      .transact(xa)
      .map(_.map(DbPublicationData.toCore))

  override def insert(entity: PublicationDataF[cats.Id]): F[Unit] = {
    val db = DbPublicationData.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, publication_id, collection_id, number_pages, link) VALUES (${db.id}, ${db.publicationId}, ${db.collectionId}, ${db.numberPages}, ${db.link})")
      .update.run.transact(xa).void
  }

  override def update(entity: PublicationDataF[cats.Id]): F[Unit] = {
    val db = DbPublicationData.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET publication_id = ${db.publicationId},
          collection_id = ${db.collectionId},
          number_pages = ${db.numberPages},
          link = ${db.link}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, PublicationDataF[Id]] =
    (fr"SELECT id, publication_id, collection_id, number_pages, link FROM" ++ Fragment.const(tableName))
      .query[DbPublicationData]
      .stream
      .transact(xa)
      .map(DbPublicationData.toCore)  
}
