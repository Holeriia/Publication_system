package publicationtracker.model

import cats.Id
import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}

object ReferenceCodecs {
  // Кодеки для Reference[Id]
  given Encoder[Reference[Id]] = deriveEncoder
  given Decoder[Reference[Id]] = deriveDecoder
}
