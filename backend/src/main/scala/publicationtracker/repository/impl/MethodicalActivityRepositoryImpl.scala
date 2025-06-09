package publicationtracker.repository.impl

import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import fs2.Stream
import publicationtracker.model.Achievements.MethodicalActivity
import publicationtracker.model.db.DbMethodicalActivity
import publicationtracker.repository.MethodicalActivityRepository

import java.util.UUID

class MethodicalActivityRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends MethodicalActivityRepository[F] {

  private val tableName = "methodical_activity"

  implicit val readDb: Read[DbMethodicalActivity] =
    Read[(UUID, String, UUID, Option[Int], Option[String])].map {
      case (id, name, typeId, pages, path) =>
        DbMethodicalActivity(id, name, typeId, pages, path)
    }

  implicit val writeDb: Write[DbMethodicalActivity] =
    Write[(UUID, String, UUID, Option[Int], Option[String])].contramap { d =>
      (d.id, d.name, d.typeId, d.numberOfPages, d.filePath)
    }

  override def getAll: F[List[MethodicalActivity]] =
    (fr"SELECT id, name, type_id, number_of_pages, file_path FROM " ++ Fragment.const(tableName))
      .query[DbMethodicalActivity]
      .to[List]
      .transact(xa)
      .map(_.map(DbMethodicalActivity.toCore))

  override def getById(id: UUID): F[Option[MethodicalActivity]] =
    (fr"SELECT id, name, type_id, number_of_pages, file_path FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbMethodicalActivity]
      .option
      .transact(xa)
      .map(_.map(DbMethodicalActivity.toCore))

  override def insert(entity: MethodicalActivity): F[Unit] = {
    val db = DbMethodicalActivity.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, name, type_id, number_of_pages, file_path) VALUES (${db.id}, ${db.name}, ${db.typeId}, ${db.numberOfPages}, ${db.filePath})")
      .update.run.transact(xa).void
  }

  override def update(entity: MethodicalActivity): F[Unit] = {
    val db = DbMethodicalActivity.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET name = ${db.name},
          type_id = ${db.typeId},
          number_of_pages = ${db.numberOfPages},
          file_path = ${db.filePath}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, MethodicalActivity] =
    (fr"SELECT id, employee_id, methodical_type_id, description FROM" ++ Fragment.const(tableName))
      .query[DbMethodicalActivity]
      .stream
      .transact(xa)
      .map(DbMethodicalActivity.toCore)  
}
