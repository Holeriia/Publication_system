package publicationtracker.repository.impl

import cats.data.NonEmptyList
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import fs2.Stream
import publicationtracker.model.CoreEntities.Author
import publicationtracker.model.db.DbAuthor
import publicationtracker.repository.AuthorRepository

import java.util.UUID

class AuthorRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends AuthorRepository[F] {

  private val tableName = "author"

  override def getAll: F[List[Author]] =
    (fr"SELECT id, first_name, last_name, patronymic, is_employee, employee_id, affiliation FROM " ++ Fragment.const(tableName))
      .query[DbAuthor]
      .to[List]
      .transact(xa)
      .map(_.map(DbAuthor.toCore))

  override def getById(id: UUID): F[Option[Author]] =
    (fr"SELECT id, first_name, last_name, patronymic, is_employee, employee_id, affiliation FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbAuthor]
      .option
      .transact(xa)
      .map(_.map(DbAuthor.toCore))

  override def insert(author: Author): F[Unit] = {
    val db = DbAuthor.fromCore(author)
    val insertFragment =
      fr"INSERT INTO " ++ Fragment.const(tableName) ++
        fr"(id, first_name, last_name, patronymic, is_employee, employee_id, affiliation) VALUES " ++
        fr"(${db.id}, ${db.firstName}, ${db.lastName}, ${db.patronymic}, ${db.isEmployee}, ${db.employeeId}, ${db.affiliation})"

    insertFragment.update.run.transact(xa).void
  }

  override def update(author: Author): F[Unit] = {
    val db = DbAuthor.fromCore(author)
    (fr"UPDATE " ++ Fragment.const(tableName) ++
      fr"""
      SET first_name = ${db.firstName},
          last_name = ${db.lastName},
          patronymic = ${db.patronymic},
          is_employee = ${db.isEmployee},
          employee_id = ${db.employeeId},
          affiliation = ${db.affiliation}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, Author] =
    (fr"SELECT id, first_name, last_name, patronymic, is_employee, employee_id, affiliation FROM " ++ Fragment.const(tableName))
      .query[DbAuthor]
      .stream
      .transact(xa)
      .map(DbAuthor.toCore)

  override def getByEmployeeId(employeeId: UUID): F[List[Author]] =
    sql"""SELECT * FROM author WHERE employee_id = $employeeId"""
      .query[Author]
      .to[List]
      .transact(xa)

  override def getByIds(ids: List[UUID]): F[List[Author]] =
    ids match {
      case Nil => Async[F].pure(Nil)
      case nonEmptyIds =>
        val nel = NonEmptyList.fromListUnsafe(nonEmptyIds)
        val query = fr"SELECT id, full_name, employee_id FROM " ++ Fragment.const(tableName) ++ Fragments.in(fr"id", nel)

        query.query[DbAuthor]
          .to[List]
          .transact(xa)
          .map(_.map(DbAuthor.toCore))
    }  
}