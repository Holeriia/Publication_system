package publicationtracker.repository.impl

import cats.Id
import cats.data.NonEmptyList
import cats.effect.MonadCancelThrow
import cats.syntax.all.*
import doobie.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import publicationtracker.model.ReferenceData.ReferenceF
import publicationtracker.model.db.{Conversions, DbReference}
import publicationtracker.repository.ReferenceRepository

import java.util.UUID

abstract class AbstractReferenceRepository[F[_]: MonadCancelThrow](tableName: String)(xa: Transactor[F])
  extends ReferenceRepository[F] {

  import doobie.implicits.*

  implicit val getDbReference: Read[DbReference] =
    Read[(UUID, String)].map { case (id, name) => DbReference(id, name) }

  implicit val putDbReference: Write[DbReference] =
    Write[(UUID, String)].contramap(d => (d.id, d.name))

  override def getAll: F[List[ReferenceF[Id]]] =
    (fr"SELECT id, name FROM " ++ Fragment.const(tableName))
      .query[DbReference]
      .to[List]
      .transact(xa)
      .map(_.map(Conversions.fromCore))

  override def getById(id: UUID): F[Option[ReferenceF[Id]]] =
    (fr"SELECT id, name FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbReference]
      .option
      .transact(xa)
      .map(_.map(Conversions.fromCore))

  override def insert(entity: ReferenceF[Id]): F[Unit] = {
    val dbRef = Conversions.toCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++ fr" (id, name) VALUES (${dbRef.id}, ${dbRef.name})")
      .update
      .run
      .transact(xa)
      .void
  }

  override def insertMany(entities: NonEmptyList[ReferenceF[Id]]): F[Unit] = {
    val dbRefs = entities.toList.map(Conversions.toCore)
    val sql = s"INSERT INTO $tableName (id, name) VALUES (?, ?)"
    Update[DbReference](sql)
      .updateMany(dbRefs)
      .transact(xa)
      .void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update
      .run
      .transact(xa)
      .map(_ > 0)

  override def findByName(name: String): F[Option[ReferenceF[Id]]] = {
    (fr"SELECT id, name FROM " ++ Fragment.const(tableName) ++ fr" WHERE name = $name")
      .query[DbReference]
      .option
      .transact(xa)
      .map(_.map(Conversions.fromCore))
  }
}
