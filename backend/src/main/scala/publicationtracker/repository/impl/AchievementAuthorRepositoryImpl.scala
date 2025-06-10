package publicationtracker.repository.impl

import cats.Id
import cats.data.NonEmptyList
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.fragments
import fs2.Stream
import publicationtracker.model.Achievements.{AchievementAuthor, AchievementAuthorF}
import publicationtracker.model.db.DbAchievementAuthor
import publicationtracker.repository.AchievementAuthorRepository

import java.util.UUID

class AchievementAuthorRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends AchievementAuthorRepository[F] {

  private val tableName = "achievement_author"

  // Read instance для doobie
  implicit val getDb: Read[DbAchievementAuthor] =
    Read[(UUID, UUID, UUID, Option[Int])].map {
      case (id, achieventId, authorId, authorOrder) =>
        DbAchievementAuthor(id, achieventId, authorId, authorOrder)
    }

  // Write instance для doobie
  implicit val putDb: Write[DbAchievementAuthor] =
    Write[(UUID, UUID, UUID, Option[Int])].contramap { db =>
      (db.id, db.achieventId, db.authorId, db.authorOrder)
    }

  override def getAll: F[List[AchievementAuthorF[Id]]] =
    (fr"SELECT id, achievent_id, author_id, author_order FROM " ++ Fragment.const(tableName))
      .query[DbAchievementAuthor]
      .to[List]
      .transact(xa)
      .map(_.map(DbAchievementAuthor.toCore))

  override def getById(id: UUID): F[Option[AchievementAuthorF[Id]]] =
    (fr"SELECT id, achievent_id, author_id, author_order FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbAchievementAuthor]
      .option
      .transact(xa)
      .map(_.map(DbAchievementAuthor.toCore))

  override def insert(entity: AchievementAuthorF[Id]): F[Unit] = {
    val db = DbAchievementAuthor.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++ fr"""
      (id, achievent_id, author_id, author_order)
      VALUES (${db.id}, ${db.achieventId}, ${db.authorId}, ${db.authorOrder})
    """).update.run.transact(xa).void
  }

  override def update(entity: AchievementAuthorF[Id]): F[Unit] = {
    val db = DbAchievementAuthor.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET achievent_id = ${db.achieventId},
          author_id = ${db.authorId},
          author_order = ${db.authorOrder}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, AchievementAuthorF[Id]] =
    (fr"SELECT id, achievent_id, author_id, author_order FROM " ++ Fragment.const(tableName))
      .query[DbAchievementAuthor]
      .stream
      .transact(xa)
      .map(DbAchievementAuthor.toCore)

  override def getByAuthorIds(authorIds: NonEmptyList[UUID]): F[List[AchievementAuthorF[Id]]] = {
    val select = fr"SELECT id, achievent_id, author_id, author_order FROM" ++ Fragment.const(tableName)
    val where = fragments.in(fr"author_id", authorIds)
    (select ++ fr" WHERE " ++ where)
      .query[DbAchievementAuthor]
      .to[List]
      .transact(xa)
      .map(_.map(DbAchievementAuthor.toCore))
  }
}