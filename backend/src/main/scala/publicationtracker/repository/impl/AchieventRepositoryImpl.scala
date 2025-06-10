package publicationtracker.repository.impl

import cats.Id
import cats.data.NonEmptyList
import cats.effect.Async
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import fs2.Stream
import publicationtracker.model.Achievements.AchieventF
import publicationtracker.model.db.DbAchievent
import publicationtracker.model.view.OtherAchievementView
import publicationtracker.repository.AchieventRepository
import io.circe.parser._ // Для парсинга JSON
import io.circe.generic.auto._
import io.circe.syntax._ // Для asJson

import java.util.UUID
import java.time.LocalDate

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

  // Этот метод не используется напрямую AchieventServiceImpl, его можно удалить или оставить для других целей
  def findByEmployeeAndType(employeeId: UUID, typeId: UUID): F[List[AchieventF[Id]]] = {
    sql"""
      SELECT id, type_id, publication_id, other_id, methodical_activity_id,
             professional_development_id, pattents_and_registration_id,
             comment, reward_file, date -- Убедись, что все колонки AchieventF[Id] есть здесь
      FROM achievent
      WHERE employee_id = $employeeId AND type_id = $typeId
    """.query[AchieventF[Id]].to[List].transact(xa)
  }

  override def getOtherAchievementsByEmployee(employeeId: UUID, otherTypeId: UUID): F[List[OtherAchievementView]] = {
    sql"""
      SELECT
        a.id,
        o.name,
        o.description,
        a.comment,
        a.date,
        coalesce(
          json_agg(
            DISTINCT (auth_details.last_name || ' ' || auth_details.first_name || ' ' || coalesce(auth_details.patronymic, ''))
            ORDER BY (auth_details.last_name || ' ' || auth_details.first_name || ' ' || coalesce(auth_details.patronymic, ''))
          ) FILTER (WHERE auth_details.id IS NOT NULL), -- И здесь auth_details
          '[]'
        )::text AS co_authors_json_string
      FROM achievent a
      JOIN other o ON a.other_id = o.id
      -- Эти джойны для ФИЛЬТРАЦИИ: найти достижения, где employeeId является АВТОРОМ
      JOIN achievement_author aa_filter ON aa_filter.achievent_id = a.id
      JOIN author au_filter ON au_filter.id = aa_filter.author_id
      -- Эти джойны для СБОРА ВСЕХ АВТОРОВ для json_agg (даже если они не employeeId)
      LEFT JOIN achievement_author aa_all ON aa_all.achievent_id = a.id
      LEFT JOIN author auth_details ON auth_details.id = aa_all.author_id
      WHERE a.type_id = $otherTypeId
        AND au_filter.employee_id = $employeeId
      GROUP BY a.id, o.name, o.description, a.comment, a.date
      ORDER BY a.date DESC NULLS LAST
    """
      .query[(UUID, String, Option[String], Option[String], Option[java.time.LocalDate], String)]
      .map { case (id, name, desc, comment, date, coAuthorsJson) =>
        val parsedCoAuthors: List[String] = decode[List[String]](coAuthorsJson).getOrElse {
          System.err.println(s"Ошибка парсинга coAuthorsJson: $coAuthorsJson для достижения $id")
          List.empty
        }
        OtherAchievementView(id, name, desc, comment, date, parsedCoAuthors)
      }
      .to[List]
      .transact(xa)
  }
}