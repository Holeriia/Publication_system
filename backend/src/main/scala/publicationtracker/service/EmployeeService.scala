package publicationtracker.service

import cats.effect.Async
import fs2.Stream
import publicationtracker.model.CoreEntities.{Employee, EmployeeF}

import java.util.UUID

trait EmployeeService[F[_]] {
  def getAll: F[List[Employee]]
  def getById(id: UUID): F[Option[Employee]]
  def create(employee: Employee): F[Unit]
  def update(employee: Employee): F[Unit]
  def delete(id: UUID): F[Boolean]
}

