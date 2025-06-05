package publicationtracker.repository.impl

import cats.effect.MonadCancelThrow
import doobie.Transactor

class OrganisationTypeRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("organisation_type")(xa)

class CityRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("city")(xa)

class AchievementTypeRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("achievement_type")(xa)

class AcademicDegreeRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("academic_degree")(xa)

class AcademicTitleRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("academic_title")(xa)

class EmployeePostRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("employee_post")(xa)

class MethodicalTypeRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("methodical_type")(xa)

class ConferenceLevelRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("conference_level")(xa)

class CollectionTypeRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("collection_type")(xa)

class PublicationLevelRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("publication_level")(xa)

class PublicationTypeRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("publication_type")(xa)

class ConferenceDataStatusRepository[F[_]: MonadCancelThrow](xa: Transactor[F])
  extends AbstractReferenceRepository[F]("conference_data_status")(xa)
