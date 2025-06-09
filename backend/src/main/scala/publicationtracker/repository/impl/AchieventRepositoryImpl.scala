package publicationtracker.repository.impl

import cats.Id
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import fs2.Stream
import publicationtracker.model.Achievements.AchieventF
import publicationtracker.model.db.DbAchievent
import publicationtracker.repository.AchieventRepository

import java.util.UUID
import cats.data.NonEmptyList

class AchieventRepositoryImpl[F[_]: Async](xa: Transactor[F]) extends AchieventRepository[F] {

  private val tableName = "achievent"

  private val selectFields =
    fr"""
      SELECT id, type_id, publication_id, other_id, methodical_activity_id,
             professional_development_id, pattents_and_registration_id,
             comment, reward_file, date
      FROM
    """ ++ Fragment.const(tableName)

  override def getAll: F[List[AchieventF[Id]]] =
    selectFields
      .query[DbAchievent]
      .to[List]
      .transact(xa)
      .map(_.map(DbAchievent.toCore))

  override def getById(id: UUID): F[Option[AchieventF[Id]]] =
    (selectFields ++ fr" WHERE id = $id")
      .query[DbAchievent]
      .option
      .transact(xa)
      .map(_.map(DbAchievent.toCore))

  override def insert(achievent: AchieventF[Id]): F[Unit] = {
    val db = DbAchievent.fromCore(achievent)
    (fr"INSERT INTO " ++ Fragment.const(tableName) ++ fr"""
      (id, type_id, publication_id, other_id, methodical_activity_id,
       professional_development_id, pattents_and_registration_id,
       comment, reward_file, date)
      VALUES
      (${db.id}, ${db.typeId}, ${db.publicationId}, ${db.otherId}, ${db.methodicalActivityId},
       ${db.professionalDevelopmentId}, ${db.pattentsAndRegistrationId},
       ${db.comment}, ${db.rewardFile}, ${db.date})
    """).update.run.transact(xa).void
  }

  override def update(achievent: AchieventF[Id]): F[Unit] = {
    val db = DbAchievent.fromCore(achievent)
    (fr"UPDATE " ++ Fragment.const(tableName) ++ fr"""
      SET type_id = ${db.typeId},
          publication_id = ${db.publicationId},
          other_id = ${db.otherId},
          methodical_activity_id = ${db.methodicalActivityId},
          professional_development_id = ${db.professionalDevelopmentId},
          pattents_and_registration_id = ${db.pattentsAndRegistrationId},
          comment = ${db.comment},
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
    selectFields
      .query[DbAchievent]
      .stream
      .transact(xa)
      .map(DbAchievent.toCore)

  override def getByIds(ids: List[UUID]): F[List[AchieventF[Id]]] =
    ids match {
      case Nil => Async[F].pure(Nil)
      case nonEmptyIds =>
        val nel: NonEmptyList[UUID] = NonEmptyList.fromListUnsafe(nonEmptyIds)
        val query =
          fr"""
            SELECT id, type_id, publication_id, other_id, methodical_activity_id,
                   professional_development_id, pattents_and_registration_id,
                   comment, reward_file, date
            FROM """ ++ Fragment.const(tableName) ++ Fragments.in(fr"id", nel)

        query.query[DbAchievent]
          .to[List]
          .transact(xa)
          .map(_.map(DbAchievent.toCore))
    }

  def findByEmployeeAndType(employeeId: UUID, typeId: UUID): F[List[AchieventF[Id]]] = {
    // SQL-запрос, который возвращает достижения для данного сотрудника и данного типа "другое"
    // (Реализация зависит от твоей структуры таблиц, примерно что-то типа:)
    sql"""
      SELECT * FROM achievement
      WHERE employee_id = $employeeId AND type_id = $typeId
    """.query[AchieventF[Id]].to[List].transact(xa)
  }
}
