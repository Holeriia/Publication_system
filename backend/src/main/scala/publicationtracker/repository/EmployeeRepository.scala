package publicationtracker.repository

import fs2.Stream
import publicationtracker.model.CoreEntities.Employee

import java.util.UUID

trait EmployeeRepository[F[_]] {
  def getAll: F[List[Employee]]
  def getById(id: UUID): F[Option[Employee]]
  def insert(e: Employee): F[Unit]
  def update(e: Employee): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, Employee]
}