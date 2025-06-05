package publicationtracker.model

import cats.Id
import publicationtracker.tagless.{FunctionK, FunctorK}

import java.util.UUID

object CoreEntities {

  case class OrganisationF[F[_]](
                                  id: F[UUID],
                                  fullName: F[String],
                                  shortName: F[String],
                                  typeId: F[UUID], // FK на organisation_type
                                  cityId: F[UUID]  // FK на city
                                )
  type Organisation = OrganisationF[Id]

  case class EmployeeF[F[_]](
                              id: F[UUID],
                              firstName: F[String],
                              lastName: F[String],
                              patronymic: F[Option[String]],
                              degreeId: F[UUID],
                              titleId: F[UUID],
                              postId: F[UUID],
                              exp: F[Option[Int]],
                              seniority: F[Option[Int]],
                              diplomaEducation: F[Option[String]]
                            )
  type Employee = EmployeeF[Id]

  case class AuthorF[F[_]](
                            id: F[UUID],
                            firstName: F[String],
                            lastName: F[String],
                            patronymic: F[Option[String]],
                            isEmployee: F[Boolean],
                            employeeId: F[Option[UUID]],
                            affiliation: F[Option[String]]
                          )
  type Author = AuthorF[Id]

  // FunctorK для OrganisationF
  implicit val organisationFunctorK: FunctorK[OrganisationF] = new FunctorK[OrganisationF] {
    def mapK[G[_], H[_]](org: OrganisationF[G])(fk: FunctionK[G, H]): OrganisationF[H] =
      OrganisationF(
        fk(org.id),
        fk(org.fullName),
        fk(org.shortName),
        fk(org.typeId),
        fk(org.cityId)
      )
  }

  // FunctorK для EmployeeF
  implicit val employeeFunctorK: FunctorK[EmployeeF] = new FunctorK[EmployeeF] {
    def mapK[G[_], H[_]](emp: EmployeeF[G])(fk: FunctionK[G, H]): EmployeeF[H] =
      EmployeeF(
        fk(emp.id),
        fk(emp.firstName),
        fk(emp.lastName),
        fk(emp.patronymic),
        fk(emp.degreeId),
        fk(emp.titleId),
        fk(emp.postId),
        fk(emp.exp),
        fk(emp.seniority),
        fk(emp.diplomaEducation)
      )
  }

  // FunctorK для AuthorF
  implicit val authorFunctorK: FunctorK[AuthorF] = new FunctorK[AuthorF] {
    def mapK[G[_], H[_]](author: AuthorF[G])(fk: FunctionK[G, H]): AuthorF[H] =
      AuthorF(
        fk(author.id),
        fk(author.firstName),
        fk(author.lastName),
        fk(author.patronymic),
        fk(author.isEmployee),
        fk(author.employeeId),
        fk(author.affiliation)
      )
  }
}
