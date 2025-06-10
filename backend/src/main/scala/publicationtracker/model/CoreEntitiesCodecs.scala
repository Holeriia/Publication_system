package publicationtracker.model

import cats.Id
import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}
import publicationtracker.model.CoreEntities.Employee

import java.time.LocalDate
import java.util.UUID
import scala.util.Try

object CoreEntitiesCodecs {

  // LocalDate codecs
  implicit val localDateEncoder: Encoder[LocalDate] = Encoder.encodeString.contramap(_.toString)
  implicit val localDateDecoder: Decoder[LocalDate] = Decoder.decodeString.emap { str =>
    Try(LocalDate.parse(str)).toEither.left.map(_.getMessage)
  }

  // Employee codecs
  implicit val employeeEncoder: Encoder[Employee] = deriveEncoder
  implicit val employeeDecoder: Decoder[Employee] = deriveDecoder
}
