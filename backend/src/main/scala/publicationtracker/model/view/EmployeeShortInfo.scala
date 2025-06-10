package publicationtracker.model.view

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}

import java.util.UUID

case class EmployeeShortInfo(
                              id: UUID,
                              fullName: String
                            )

object EmployeeShortInfo:
  given Encoder[EmployeeShortInfo] = deriveEncoder
// given Decoder[EmployeeShortInfo] = deriveDecoder
