package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.{Get, Put, Read, Write}
import publicationtracker.model.Achievements.AchieventF
import publicationtracker.model.db.DbAchievent
import publicationtracker.repository.AchieventRepository
import fs2.Stream
import java.util.UUID

class AchieventRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends AchieventRepository[F] {

  private val tableName = "achievement"  // Имя таблицы в БД, уточни по схеме

  implicit val getDbAchievent: Read[DbAchievent] =
    Read[(UUID, UUID, Option[String], Option[java.time.LocalDate])].map {
      case (id, typeId, rewardFile, date) =>
        DbAchievent(id, typeId, rewardFile, date)
    }

  implicit val putDbAchievent: Write[DbAchievent] =
    Write[(UUID, UUID, Option[String], Option[java.time.LocalDate])].contramap { db =>
      (db.id, db.typeId, db.rewardFile, db.date)
    }

  override def getAll: F[List[AchieventF[Id]]] =
    (fr"SELECT id, type_id, reward_file, date FROM " ++ Fragment.const(tableName))
      .query[DbAchievent]
      .to[List]
      .transact(xa)
      .map(_.map(DbAchievent.toCore))

  override def getById(id: UUID): F[Option[AchieventF[Id]]] =
    (fr"SELECT id, type_id, reward_file, date FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .query[DbAchievent]
      .option
      .transact(xa)
      .map(_.map(DbAchievent.toCore))

  override def insert(achievent: AchieventF[Id]): F[Unit] = {
    val db = DbAchievent.fromCore(achievent)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++
      fr"(id, type_id, reward_file, date) VALUES (${db.id}, ${db.typeId}, ${db.rewardFile}, ${db.date})")
      .update.run.transact(xa).void
  }

  override def update(achievent: AchieventF[Id]): F[Unit] = {
    val db = DbAchievent.fromCore(achievent)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET type_id = ${db.typeId},
          reward_file = ${db.rewardFile},
          date = ${db.date}
      WHERE id = ${db.id}
    """).update.run.transact(xa).void
  }

  override def delete(id: UUID): F[Boolean] =
    (fr"DELETE FROM " ++ Fragment.const(tableName) ++ fr" WHERE id = $id")
      .update.run.transact(xa)
      .map(_ > 0)

  override def streamAll: Stream[F, AchieventF[Id]] =
    (fr"SELECT id, type_id, reward_file, date FROM achievement")
      .query[DbAchievent]
      .stream
      .transact(xa)
      .map(DbAchievent.toCore)
}
