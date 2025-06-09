package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Read, Write}
import fs2.Stream
import publicationtracker.model.ConferencesAndPublications.PublicationF
import publicationtracker.model.db.DbPublication
import publicationtracker.repository.PublicationRepository

import java.util.UUID

class PublicationRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends PublicationRepository[F] {

  private val tableName = "publication"

  override def getAll: F[List[PublicationF[Id]]] =
    (fr"SELECT id, name, level_id, type_id, number_of_pages, number_ez, number_ek, file_path FROM " ++ Fragment.const(tableName))
      .query[DbPublication]
      .to[List]
      .transact(xa)
      .map(_.map(DbPublication.toCore))

  override def getById(id: UUID): F[Option[PublicationF[Id]]] =
    (fr"SELECT id, name, level_id, type_id, number_of_pages, number_ez, number_ek, file_path FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbPublication]
      .option
      .transact(xa)
      .map(_.map(DbPublication.toCore))

  override def insert(entity: PublicationF[Id]): F[Unit] = {
    val db = DbPublication.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"""
      (id, name, level_id, type_id, number_of_pages, number_ez, number_ek, file_path)
      VALUES (${db.id}, ${db.name}, ${db.levelId}, ${db.typeId}, ${db.numberOfPages}, ${db.numberEz}, ${db.numberEk}, ${db.filePath})
    """).update.run.transact(xa).void
  }

  override def update(entity: PublicationF[Id]): F[Unit] = {
    val db = DbPublication.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++
      fr"""
      SET name = ${db.name},
          level_id = ${db.levelId},
          type_id = ${db.typeId},
          number_of_pages = ${db.numberOfPages},
          number_ez = ${db.numberEz},
          number_ek = ${db.numberEk},
          file_path = ${db.filePath}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, PublicationF[Id]] =
    (fr"""SELECT
          id,
          name,
          level_id,
          type_id,
          number_of_pages,
          number_ez,
          number_ek,
          file_path
        FROM""" ++ Fragment.const(tableName))
      .query[DbPublication]
      .stream
      .transact(xa)
      .map(DbPublication.toCore)
}