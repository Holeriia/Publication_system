package publicationtracker.repository

import publicationtracker.model.CoreEntities.Employee

import java.util.UUID
import fs2.Stream

trait EmployeeRepository[F[_]] {
  def getAll: F[List[Employee]]
  def getById(id: UUID): F[Option[Employee]]
  def insert(e: Employee): F[Unit]
  def update(e: Employee): F[Unit]
  def delete(id: UUID): F[Boolean]
  def streamAll: Stream[F, Employee]
}
