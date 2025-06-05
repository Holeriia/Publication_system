package publicationtracker.repository.impl

import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import fs2.Stream
import publicationtracker.model.CoreEntities.Employee
import publicationtracker.model.db.DbEmployee
import publicationtracker.repository.EmployeeRepository
import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import publicationtracker.model.Achievements.AchievementAuthorF
import publicationtracker.model.db.DbAchievementAuthor
import publicationtracker.repository.AchievementAuthorRepository
import fs2.Stream
import java.util.UUID

import java.util.UUID

class EmployeeRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends EmployeeRepository[F] {

  private val tableName = "employee"

  private val fields =
    fr"id, first_name, last_name, patronymic, degree_id, title_id, post_id, exp, seniority, diploma_education"

  override def getAll: F[List[Employee]] =
    (fr"SELECT" ++ fields ++ fr"FROM" ++ Fragment.const(tableName))
      .query[DbEmployee]
      .to[List]
      .transact(xa)
      .map(_.map(DbEmployee.toCore))

  override def getById(id: UUID): F[Option[Employee]] =
    (fr"SELECT" ++ fields ++ fr"FROM" ++ Fragment.const(tableName) ++ fr"WHERE id = $id")
      .query[DbEmployee]
      .option
      .transact(xa)
      .map(_.map(DbEmployee.toCore))

  override def insert(e: Employee): F[Unit] = {
    val db = DbEmployee.fromCore(e)
    (fr"INSERT INTO" ++ Fragment.const(tableName) ++ fr"""
      (id, first_name, last_name, patronymic, degree_id, title_id, post_id, exp, seniority, diploma_education)
      VALUES (${db.id}, ${db.firstName}, ${db.lastName}, ${db.patronymic}, ${db.degreeId}, ${db.titleId}, ${db.postId}, ${db.exp}, ${db.seniority}, ${db.diplomaEducation})
    """).update.run.transact(xa).void
  }

  override def update(e: Employee): F[Unit] = {
    val db = DbEmployee.fromCore(e)
    (fr"UPDATE" ++ Fragment.const(tableName) ++ fr"""
      SET first_name = ${db.firstName},
          last_name = ${db.lastName},
          patronymic = ${db.patronymic},
          degree_id = ${db.degreeId},
          title_id = ${db.titleId},
          post_id = ${db.postId},
          exp = ${db.exp},
          seniority = ${db.seniority},
          diploma_education = ${db.diplomaEducation}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM" ++ Fragment.const(tableName) ++ fr"WHERE id = $id")
      .update.run
      .transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, Employee] =
    (fr"SELECT" ++ fields ++ fr"FROM" ++ Fragment.const(tableName))
      .query[DbEmployee]
      .stream
      .transact(xa)
      .map(DbEmployee.toCore)
}
