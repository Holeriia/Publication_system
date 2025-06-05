package publicationtracker.service.impl

import cats.{Id, Monad}
import cats.syntax.all.*
import cats.effect.Async
import fs2.Stream
import publicationtracker.model.CoreEntities.{Employee, EmployeeF}
import publicationtracker.repository.EmployeeRepository
import publicationtracker.service.EmployeeService

import java.util.UUID

class EmployeeServiceImpl[F[_]: Monad](repo: EmployeeRepository[F]) extends EmployeeService[F] {
  def getAll: F[List[Employee]] = repo.getAll
  def getById(id: UUID): F[Option[Employee]] = repo.getById(id)
  def create(employee: Employee): F[Unit] = repo.insert(employee)
  def update(employee: Employee): F[Unit] = repo.update(employee)
  def delete(id: UUID): F[Boolean] = repo.delete(id)
}


