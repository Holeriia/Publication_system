package publicationtracker.model

import cats.Id
import publicationtracker.tagless.*

import java.time.LocalDate
import java.util.UUID

object Achievements {

  case class AchieventF[F[_]](
                               id: F[UUID],
                               typeId: F[UUID],
                               publicationId: F[Option[UUID]],
                               otherId: F[Option[UUID]],
                               methodicalActivityId: F[Option[UUID]],
                               professionalDevelopmentId: F[Option[UUID]],
                               pattentsAndRegistrationId: F[Option[UUID]],
                               comment: F[Option[String]],
                               rewardFile: F[Option[String]],
                               date: F[Option[LocalDate]]
                             )
  type Achievent = AchieventF[Id]

  case class AchievementAuthorF[F[_]](
                                       id: F[UUID],
                                       achieventId: F[UUID],
                                       authorId: F[UUID],
                                       authorOrder: F[Option[Int]]
                                     )
  type AchievementAuthor = AchievementAuthorF[Id]

  case class OtherF[F[_]](
                           id: F[UUID],
                           name: F[String],
                           description: F[Option[String]],
                           filePath: F[Option[String]]
                         )
  type Other = OtherF[Id]

  case class ProfessionalDevelopmentF[F[_]](
                                             id: F[UUID],
                                             name: F[String],
                                             dateStart: F[Option[LocalDate]],
                                             dateEnd: F[Option[LocalDate]],
                                             organisationId: F[UUID]
                                           )
  type ProfessionalDevelopment = ProfessionalDevelopmentF[Id]

  case class MethodicalActivityF[F[_]](
                                        id: F[UUID],
                                        name: F[String],
                                        typeId: F[UUID],
                                        numberOfPages: F[Option[Int]],
                                        filePath: F[Option[String]]
                                      )
  type MethodicalActivity = MethodicalActivityF[Id]

  case class PattentsAndRegistrationF[F[_]](
                                             id: F[UUID],
                                             name: F[String],
                                             number: F[Option[String]],
                                             registrationDate: F[Option[LocalDate]],
                                             note: F[Option[String]]
                                           )
  type PattentsAndRegistration = PattentsAndRegistrationF[Id]

  // FunctorK для AchieventF
  implicit val achieventFunctorK: FunctorK[AchieventF] = new FunctorK[AchieventF] {
    override def mapK[G[_], H[_]](fa: AchieventF[G])(fk: FunctionK[G, H]): AchieventF[H] =
      AchieventF(
        fk(fa.id),
        fk(fa.typeId),

        fk(fa.publicationId),
        fk(fa.otherId),
        fk(fa.methodicalActivityId),
        fk(fa.professionalDevelopmentId),
        fk(fa.pattentsAndRegistrationId),

        fk(fa.comment),
        fk(fa.rewardFile),
        fk(fa.date)
      )
  }

  // FunctorK для AchievementAuthorF
  implicit val achievementAuthorFunctorK: FunctorK[AchievementAuthorF] = new FunctorK[AchievementAuthorF] {
    override def mapK[G[_], H[_]](fa: AchievementAuthorF[G])(fk: FunctionK[G, H]): AchievementAuthorF[H] =
      AchievementAuthorF(
        fk(fa.id),
        fk(fa.achieventId),
        fk(fa.authorId),
        fk(fa.authorOrder)
      )
  }

  // FunctorK для OtherF
  implicit val otherFunctorK: FunctorK[OtherF] = new FunctorK[OtherF] {
    def mapK[G[_], H[_]](fa: OtherF[G])(fk: FunctionK[G, H]): OtherF[H] =
      OtherF(
        fk(fa.id),
        fk(fa.name),
        fk(fa.description),
        fk(fa.filePath)
      )
  }

  // FunctorK для ProfessionalDevelopmentF
  implicit val professionalDevelopmentFunctorK: FunctorK[ProfessionalDevelopmentF] = new FunctorK[ProfessionalDevelopmentF] {
    def mapK[G[_], H[_]](fa: ProfessionalDevelopmentF[G])(fk: FunctionK[G, H]): ProfessionalDevelopmentF[H] =
      ProfessionalDevelopmentF(
        fk(fa.id),
        fk(fa.name),
        fk(fa.dateStart),
        fk(fa.dateEnd),
        fk(fa.organisationId)
      )
  }

  // FunctorK для MethodicalActivityF
  implicit val methodicalActivityFunctorK: FunctorK[MethodicalActivityF] = new FunctorK[MethodicalActivityF] {
    def mapK[G[_], H[_]](fa: MethodicalActivityF[G])(fk: FunctionK[G, H]): MethodicalActivityF[H] =
      MethodicalActivityF(
        fk(fa.id),
        fk(fa.name),
        fk(fa.typeId),
        fk(fa.numberOfPages),
        fk(fa.filePath)
      )
  }

  // FunctorK для PattentsAndRegistrationF
  implicit val pattentsAndRegistrationFunctorK: FunctorK[PattentsAndRegistrationF] = new FunctorK[PattentsAndRegistrationF] {
    def mapK[G[_], H[_]](fa: PattentsAndRegistrationF[G])(fk: FunctionK[G, H]): PattentsAndRegistrationF[H] =
      PattentsAndRegistrationF(
        fk(fa.id),
        fk(fa.name),
        fk(fa.number),
        fk(fa.registrationDate),
        fk(fa.note)
      )
  }

}
