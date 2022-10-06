package net.wiringbits.repositories.daos

import net.wiringbits.common.models.{BabyName}
import net.wiringbits.repositories.models.Baby

import java.sql.Connection
import java.util.UUID

object BabyDAO {

  import anorm._

  def create(request: Baby.CreateBaby)(implicit conn: Connection): Unit = {
    val _ = SQL"""
        INSERT INTO baby_names
          ( baby_name, baby_date, created_at)
        VALUES (
          ${request.name.string},
          ${request.babyDate},
          NOW()
        )
        """
      .execute()
  }

  def all()(implicit conn: Connection): List[Baby] = {
    SQL"""
        SELECT id, baby_name, baby_date, baby_era_id, created_at
        FROM baby_names
        """.as(babyParser.*)
  }

  def find(babyName: BabyName)(implicit conn: Connection): Option[Baby] = {
    SQL"""
        SELECT id, baby_name, baby_date, baby_era_id, created_at
        FROM baby_names
        WHERE baby_name ILIKE ${babyName.string}
        """.as(babyParser.singleOpt)
  }

  def find(id: Int)(implicit conn: Connection): Option[Baby] = {
    SQL"""
        SELECT id, baby_name, baby_date, baby_era_id, created_at
        FROM baby_names
        WHERE id = $id
        """.as(babyParser.singleOpt)
  }

  def updateName(id: Int, name: BabyName)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      UPDATE baby_names
      SET baby_name = ${name.string}
      WHERE id = $id
    """.execute()
  }

  def findBabyForUpdate(id: UUID)(implicit conn: Connection): Option[Baby] = {
    SQL"""
        SELECT id, baby_name, baby_date, baby_era_id, created_at
        FROM baby_names
        WHERE id = $id
        FOR UPDATE NOWAIT
        """.as(babyParser.singleOpt)
  }
}
