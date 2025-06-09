package publicationtracker.repository.impl

import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import fs2.Stream
import publicationtracker.model.CoreEntities.Employee
import publicationtracker.model.db.DbEmployee
import publicationtracker.repository.EmployeeRepository

import java.util.UUID

class EmployeeRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends EmployeeRepository[F] {

  private val tableName = "employee"

  private val fields =
    fr"id, first_name, last_name, patronymic, degree_id, title_id, post_id, university_start_date, industry_start_date, experience_comment, diploma_education"

  override def getAll: F[List[Employee]] =
    (fr"SELECT" ++ fields ++ fr" FROM " ++ Fragment.const(tableName))
      .query[DbEmployee]
      .to[List]
      .transact(xa)
      .map(_.map(DbEmployee.toCore))

  override def getById(id: UUID): F[Option[Employee]] =
    (fr"SELECT" ++ fields ++ fr" FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbEmployee]
      .option
      .transact(xa)
      .map(_.map(DbEmployee.toCore))

  override def insert(e: Employee): F[Unit] = {
    val db = DbEmployee.fromCore(e)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++ fr"""
      (id, first_name, last_name, patronymic, degree_id, title_id, post_id, university_start_date, industry_start_date, experience_comment, diploma_education)
      VALUES (${db.id}, ${db.firstName}, ${db.lastName}, ${db.patronymic}, ${db.degreeId}, ${db.titleId}, ${db.postId}, ${db.universityStartDate}, ${db.industryStartDate}, ${db.experienceComment}, ${db.diplomaEducation})
    """).update.run.transact(xa).void
  }

  override def update(e: Employee): F[Unit] = {
    val db = DbEmployee.fromCore(e)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET first_name = ${db.firstName},
          last_name = ${db.lastName},
          patronymic = ${db.patronymic},
          degree_id = ${db.degreeId},
          title_id = ${db.titleId},
          post_id = ${db.postId},
          university_start_date = ${db.universityStartDate},
          industry_start_date = ${db.industryStartDate},
          experience_comment = ${db.experienceComment},
          diploma_education = ${db.diplomaEducation}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run
      .transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, Employee] =
    (fr"SELECT" ++ fields ++ fr" FROM " ++ Fragment.const(tableName))
      .query[DbEmployee]
      .stream
      .transact(xa)
      .map(DbEmployee.toCore)
  
}

