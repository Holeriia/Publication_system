package publicationtracker.model

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._
import cats.Id

object ReferenceCodecs {
  // Кодеки для Reference[Id]
  given Encoder[Reference[Id]] = deriveEncoder
  given Decoder[Reference[Id]] = deriveDecoder
}
