package publicationtracker.model

import publicationtracker.tagless.{FunctionK, FunctorK}

import java.util.UUID

// Общая HKD-модель справочника с id и name
case class Reference[F[_]](id: F[UUID], name: F[String])

object ReferenceData {
  type ReferenceF[F[_]] = Reference[F]
  type OrganisationTypeF[F[_]] = Reference[F]
  type CityF[F[_]] = Reference[F]
  type AchievementTypeF[F[_]] = Reference[F]
  type AcademicDegreeF[F[_]] = Reference[F]
  type AcademicTitleF[F[_]] = Reference[F]
  type EmployeePostF[F[_]] = Reference[F]
  type MethodicalTypeF[F[_]] = Reference[F]
  type ConferenceLevelF[F[_]] = Reference[F]
  type CollectionTypeF[F[_]] = Reference[F]
  type PublicationLevelF[F[_]] = Reference[F]
  type PublicationTypeF[F[_]] = Reference[F]
  type ConferenceDataStatusF[F[_]] = Reference[F]

  // FunctorK для Reference — позволяет применять FunctionK к полям id и name
  implicit val referenceFunctorK: FunctorK[Reference] = new FunctorK[Reference] {
    override def mapK[G[_], H[_]](rf: Reference[G])(fk: FunctionK[G, H]): Reference[H] =
      Reference(fk(rf.id), fk(rf.name))
  }
}
