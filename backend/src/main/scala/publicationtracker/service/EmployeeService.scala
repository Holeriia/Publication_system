package publicationtracker.service

import publicationtracker.model.CoreEntities.Employee
import publicationtracker.model.view.{EmployeeFull, EmployeeFullResponse, OtherAchievementView}

import java.util.UUID

trait EmployeeService[F[_]] {
  def getAll: F[List[Employee]]
  def getById(id: UUID): F[Option[Employee]]
  def create(employee: Employee): F[Unit]
  def update(employee: Employee): F[Unit]
  def delete(id: UUID): F[Boolean]
  def getFull(id: UUID): F[Option[EmployeeFull]]
  def getFullWithOtherAchievements(id: UUID): F[Option[(EmployeeFull, List[OtherAchievementView])]]
}