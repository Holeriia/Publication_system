package publicationtracker.dao

import cats.effect.IO
import doobie.*
import doobie.implicits.*

final case class Publication(id: Long, title: String, author: String)

class PublicationDao(xa: Transactor[IO]) {

  def findAll: IO[List[Publication]] =
    sql"SELECT id, title, author FROM publications"
      .query[Publication]
      .to[List]
      .transact(xa)

  def insert(pub: Publication): IO[Int] =
    sql"INSERT INTO publications (id, title, author) VALUES (${pub.id}, ${pub.title}, ${pub.author})"
      .update
      .run
      .transact(xa)
}
