package publicationtracker.service.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import publicationtracker.model.CoreEntities.Employee
import publicationtracker.model.ReferenceData.ReferenceF
import publicationtracker.model.view.EmployeeFull
import publicationtracker.repository.EmployeeRepository
import publicationtracker.repository.impl.{AcademicDegreeRepository, AcademicTitleRepository, EmployeePostRepository}
import publicationtracker.service.EmployeeService

import java.time.format.DateTimeFormatter
import java.util.UUID

class EmployeeServiceImpl[F[_]: Async](
                                        employeeRepo: EmployeeRepository[F],
                                        degreeRepo: AcademicDegreeRepository[F],
                                        titleRepo: AcademicTitleRepository[F],
                                        postRepo: EmployeePostRepository[F]
                                      ) extends EmployeeService[F] {

  private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

  def getAll: F[List[Employee]] = employeeRepo.getAll
  def getById(id: UUID): F[Option[Employee]] = employeeRepo.getById(id)
  def create(employee: Employee): F[Unit] = employeeRepo.insert(employee)
  def update(employee: Employee): F[Unit] = employeeRepo.update(employee)
  def delete(id: UUID): F[Boolean] = employeeRepo.delete(id)

  def getFull(id: UUID): F[Option[EmployeeFull]] =
    for {
      maybeEmp <- employeeRepo.getById(id)
      maybeResponse <- maybeEmp match {
        case None => Async[F].pure(None)
        case Some(emp) =>
          val degreeF: F[Option[ReferenceF[Id]]] = emp.degreeId match {
            case Some(id) => degreeRepo.getById(id)
            case None => Async[F].pure(None)
          }
          val titleF: F[Option[ReferenceF[Id]]] = emp.titleId match {
            case Some(id) => titleRepo.getById(id)
            case None => Async[F].pure(None)
          }
          val postF: F[Option[ReferenceF[Id]]] = emp.postId match {
            case Some(id) => postRepo.getById(id)
            case None => Async[F].pure(None)
          }
          (degreeF, titleF, postF).mapN { (degreeOpt, titleOpt, postOpt) =>
            EmployeeFull(
              id = emp.id,
              firstName = emp.firstName,
              lastName = emp.lastName,              patronymic = emp.patronymic,
              degree = degreeOpt,
              title = titleOpt,
              post = postOpt,
              universityStartDate = emp.universityStartDate.map(_.format(dateFormatter)),
              industryStartDate = emp.industryStartDate.map(_.format(dateFormatter)),
              experienceComment = emp.experienceComment,
              diplomaEducation = emp.diplomaEducation
            )
          }.map(Some(_))
      }
    } yield maybeResponse
}
