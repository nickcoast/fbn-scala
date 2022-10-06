package net.wiringbits.repositories.daos

import net.wiringbits.common.models.{EraName}
import net.wiringbits.repositories.models.Era

import java.sql.Connection
import java.util.UUID

object EraDAO {

  import anorm._

  def create(request: Era.CreateEra)(implicit conn: Connection): Unit = {
    val _ =
      SQL"""
        INSERT INTO eras
          (era_name, start_date, created_at)
        VALUES (
          ${request.name.string},
          ${request.startDate},
          NOW()
        )
        """
        .execute()
  }

  def all()(implicit conn: Connection): List[Era] = {
    SQL"""
        SELECT id, era_name, story, start_date, end_date, created_at
        FROM eras
        """.as(eraParser.*)
  }

  def find(eraName: EraName)(implicit conn: Connection): Option[Era] = {
    SQL"""
        SELECT id, era_name, story, start_date, end_date, created_at
        FROM eras
        WHERE era_name ILIKE ${eraName.string}
        """.as(eraParser.singleOpt)
  }

  def find(eraId: Int)(implicit conn: Connection): Option[Era] = {
    SQL"""
        SELECT id, era_name, story, start_date, end_date, created_at
        FROM eras
        WHERE id = $eraId
        """.as(eraParser.singleOpt)
  }

  def updateName(eraId: Int, name: EraName)(implicit conn: Connection): Unit = {
    val _ =
      SQL"""
      UPDATE eras
      SET era_name = ${name.string}
      WHERE id = $eraId
    """.execute()
  }

  def findEraForUpdate(eraId: Int)(implicit conn: Connection): Option[Era] = {
    SQL"""
        SELECT id, era_name, story, start_date, end_date, created_at
        FROM eras
        WHERE user_id = $eraId
        FOR UPDATE NOWAIT
        """.as(eraParser.singleOpt)
  }
}
