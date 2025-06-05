package publicationtracker.model

import cats.Id
import publicationtracker.tagless.*

import java.time.LocalDate
import java.util.UUID

object ConferencesAndPublications {

  case class ConferenceF[F[_]](
                                id: F[UUID],
                                name: F[String],
                                levelId: F[UUID],
                                organisationId: F[UUID],
                                date: F[Option[LocalDate]],
                                regulationFile: F[Option[String]]
                              )
  type Conference = ConferenceF[Id]

  case class CollectionDataF[F[_]](
                                    id: F[UUID],
                                    name: F[String],
                                    typeId: F[UUID],
                                    filePath: F[Option[String]],
                                    link: F[Option[String]]
                                  )
  type CollectionData = CollectionDataF[Id]

  case class PublicationF[F[_]](
                                 id: F[UUID],
                                 name: F[String],
                                 levelId: F[UUID],
                                 typeId: F[UUID],
                                 numberOfPages: F[Option[Int]],
                                 numberEz: F[Option[String]],
                                 numberEk: F[Option[String]],
                                 filePath: F[Option[String]]
                               )
  type Publication = PublicationF[Id]

  case class ConferenceDataF[F[_]](
                                    id: F[UUID],
                                    conferenceId: F[UUID],
                                    collectionId: F[UUID],
                                    statusId: F[UUID]
                                  )
  type ConferenceData = ConferenceDataF[Id]

  case class PublicationDataF[F[_]](
                                     id: F[UUID],
                                     publicationId: F[UUID],
                                     collectionId: F[UUID],
                                     numberPages: F[Option[String]],
                                     link: F[Option[String]]
                                   )
  type PublicationData = PublicationDataF[Id]

  implicit val conferenceFunctorK: FunctorK[ConferenceF] = new FunctorK[ConferenceF] {
    def mapK[G[_], H[_]](fa: ConferenceF[G])(fk: FunctionK[G, H]): ConferenceF[H] =
      ConferenceF(
        fk(fa.id),
        fk(fa.name),
        fk(fa.levelId),
        fk(fa.organisationId),
        fk(fa.date),
        fk(fa.regulationFile)
      )
  }

  implicit val collectionDataFunctorK: FunctorK[CollectionDataF] = new FunctorK[CollectionDataF] {
    def mapK[G[_], H[_]](fa: CollectionDataF[G])(fk: FunctionK[G, H]): CollectionDataF[H] =
      CollectionDataF(
        fk(fa.id),
        fk(fa.name),
        fk(fa.typeId),
        fk(fa.filePath),
        fk(fa.link)
      )
  }

  implicit val publicationFunctorK: FunctorK[PublicationF] = new FunctorK[PublicationF] {
    def mapK[G[_], H[_]](fa: PublicationF[G])(fk: FunctionK[G, H]): PublicationF[H] =
      PublicationF(
        fk(fa.id),
        fk(fa.name),
        fk(fa.levelId),
        fk(fa.typeId),
        fk(fa.numberOfPages),
        fk(fa.numberEz),
        fk(fa.numberEk),
        fk(fa.filePath)
      )
  }

  implicit val conferenceDataFunctorK: FunctorK[ConferenceDataF] = new FunctorK[ConferenceDataF] {
    def mapK[G[_], H[_]](fa: ConferenceDataF[G])(fk: FunctionK[G, H]): ConferenceDataF[H] =
      ConferenceDataF(
        fk(fa.id),
        fk(fa.conferenceId),
        fk(fa.collectionId),
        fk(fa.statusId)
      )
  }

  implicit val publicationDataFunctorK: FunctorK[PublicationDataF] = new FunctorK[PublicationDataF] {
    def mapK[G[_], H[_]](fa: PublicationDataF[G])(fk: FunctionK[G, H]): PublicationDataF[H] =
      PublicationDataF(
        fk(fa.id),
        fk(fa.publicationId),
        fk(fa.collectionId),
        fk(fa.numberPages),
        fk(fa.link)
      )
  }

}
