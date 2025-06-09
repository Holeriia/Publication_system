package publicationtracker.model.view

import publicationtracker.model.ReferenceData.ReferenceF
import java.util.UUID
import cats.Id
import io.circe.Encoder
import io.circe.generic.semiauto.*
import publicationtracker.model.ReferenceCodecs.given

final case class EmployeeFull(
                               id: UUID,
                               firstName: String,
                               lastName: String,
                               patronymic: Option[String],
                               degree: Option[ReferenceF[Id]],
                               title: Option[ReferenceF[Id]],
                               post: Option[ReferenceF[Id]],
                               universityStartDate: Option[String],
                               industryStartDate: Option[String],
                               experienceComment: Option[String],
                               diplomaEducation: Option[String]
                             )

given Encoder[EmployeeFull] = deriveEncoder
