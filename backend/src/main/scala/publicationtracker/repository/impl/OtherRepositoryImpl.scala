package publicationtracker.repository.impl

import cats.Id
import cats.data.NonEmptyList
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import fs2.Stream
import publicationtracker.model.Achievements.OtherF
import publicationtracker.model.db.{Conversions, DbOther}
import publicationtracker.repository.OtherRepository

import java.util.UUID

class OtherRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends OtherRepository[F] {

  private val tableName = "other"

  implicit val getDb: Read[DbOther] =
    Read[(UUID, String, Option[String], Option[String])].map {
      case (id, name, desc, path) =>
        DbOther(id, name, desc, path)
    }

  implicit val putDb: Write[DbOther] =
    Write[(UUID, String, Option[String], Option[String])].contramap { db =>
      (db.id, db.name, db.description, db.filePath)
    }

  override def getAll: F[List[OtherF[Id]]] =
    (fr"SELECT id, name, description, file_path FROM " ++ Fragment.const(tableName))
      .query[DbOther]
      .to[List]
      .transact(xa)
      .map(_.map(DbOther.toCore))

  override def getById(id: UUID): F[Option[OtherF[Id]]] =
    (fr"SELECT id, name, description, file_path FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbOther]
      .option
      .transact(xa)
      .map(_.map(DbOther.toCore))

  override def insert(entity: OtherF[Id]): F[Unit] = {
    val db = DbOther.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, name, description, file_path) VALUES (${db.id}, ${db.name}, ${db.description}, ${db.filePath})")
      .update.run.transact(xa).void
  }

  override def update(entity: OtherF[Id]): F[Unit] = {
    val db = DbOther.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET name = ${db.name},
          description = ${db.description},
          file_path = ${db.filePath}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, OtherF[Id]] =
    (fr"SELECT id, employee_id, description FROM" ++ Fragment.const(tableName))
      .query[DbOther]
      .stream
      .transact(xa)
      .map(DbOther.toCore)

  def getByIds(ids: List[UUID]): F[List[OtherF[Id]]] =
    ids match {
      case Nil => Async[F].pure(Nil)
      case nonEmptyIds =>
        val nel = NonEmptyList.fromListUnsafe(nonEmptyIds)
        val query = fr"SELECT id, name, description, file_path FROM other WHERE " ++ Fragments.in(fr"id", nel)
        query.query[DbOther]
          .to[List]
          .transact(xa)
          .map(_.map(DbOther.toCore))
    }
}
