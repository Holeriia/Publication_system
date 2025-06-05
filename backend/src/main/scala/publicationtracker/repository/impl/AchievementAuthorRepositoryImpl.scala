package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import publicationtracker.model.Achievements.AchievementAuthorF
import publicationtracker.model.db.DbAchievementAuthor
import publicationtracker.repository.AchievementAuthorRepository
import fs2.Stream
import java.util.UUID

class AchievementAuthorRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends AchievementAuthorRepository[F] {

  private val tableName = "achievement_author"

  implicit val getDb: Read[DbAchievementAuthor] =
    Read[
      (
        UUID, UUID, UUID,
          Option[UUID], Option[UUID], Option[UUID],
          Option[UUID], Option[UUID], Option[Int]
        )
    ].map {
      case (id, achieventId, authorId, pubId, otherId, methId, profDevId, patentId, order) =>
        DbAchievementAuthor(id, achieventId, authorId, pubId, otherId, methId, profDevId, patentId, order)
    }

  implicit val putDb: Write[DbAchievementAuthor] =
    Write[
      (
        UUID, UUID, UUID,
          Option[UUID], Option[UUID], Option[UUID],
          Option[UUID], Option[UUID], Option[Int]
        )
    ].contramap { db =>
      (
        db.id, db.achieventId, db.authorId,
        db.publicationId, db.otherId, db.methodicalActivityId,
        db.professionalDevelopmentId, db.pattentsAndRegistrationId, db.authorOrder
      )
    }

  override def getAll: F[List[AchievementAuthorF[Id]]] =
    (fr"SELECT id, achievent_id, author_id, publication_id, other_id, methodical_activity_id, professional_development_id, pattents_and_registration_id, author_order FROM " ++ Fragment.const(tableName))
      .query[DbAchievementAuthor]
      .to[List]
      .transact(xa)
      .map(_.map(DbAchievementAuthor.toCore))

  override def getById(id: UUID): F[Option[AchievementAuthorF[Id]]] =
    (fr"SELECT id, achievent_id, author_id, publication_id, other_id, methodical_activity_id, professional_development_id, pattents_and_registration_id, author_order FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbAchievementAuthor]
      .option
      .transact(xa)
      .map(_.map(DbAchievementAuthor.toCore))

  override def insert(entity: AchievementAuthorF[Id]): F[Unit] = {
    val db = DbAchievementAuthor.fromCore(entity)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"""(id, achievent_id, author_id, publication_id, other_id, methodical_activity_id, professional_development_id, pattents_and_registration_id, author_order)
           VALUES (${db.id}, ${db.achieventId}, ${db.authorId}, ${db.publicationId}, ${db.otherId}, ${db.methodicalActivityId}, ${db.professionalDevelopmentId}, ${db.pattentsAndRegistrationId}, ${db.authorOrder})""")
      .update.run.transact(xa).void
  }

  override def update(entity: AchievementAuthorF[Id]): F[Unit] = {
    val db = DbAchievementAuthor.fromCore(entity)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET achievent_id = ${db.achieventId},
          author_id = ${db.authorId},
          publication_id = ${db.publicationId},
          other_id = ${db.otherId},
          methodical_activity_id = ${db.methodicalActivityId},
          professional_development_id = ${db.professionalDevelopmentId},
          pattents_and_registration_id = ${db.pattentsAndRegistrationId},
          author_order = ${db.authorOrder}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, AchievementAuthorF[Id]] =
    (fr"SELECT id, achievement_id, author_id FROM" ++ Fragment.const(tableName))
      .query[DbAchievementAuthor]
      .stream
      .transact(xa)
      .map(DbAchievementAuthor.toCore)  
}
