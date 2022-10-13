package net.wiringbits.repositories.daos

import anorm.SqlParser.scalar
import net.wiringbits.common.models.{BabyName, Name}
import net.wiringbits.repositories.models.Baby

import java.sql.Connection
import java.util.UUID
import java.time.Instant

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

  def updateName(babyName: BabyName, name: BabyName)(implicit conn: Connection): Unit = {
    val _ = SQL"""
      UPDATE baby_names
      SET baby_name = ${name.string}
      WHERE baby_name LIKE ${babyName.string}
    """.execute()
  }

  def findBabyForUpdate(id: Int)(implicit conn: Connection): Option[Baby] = {
    SQL"""
        SELECT id, baby_name, baby_date, baby_era_id, created_at
        FROM baby_names
        WHERE id = $id
        FOR UPDATE NOWAIT
        """.as(babyParser.singleOpt)
  }

  def get(parent1Name: Name, parent2Name: Name)(implicit conn: Connection): Option[Baby] = {
    val total = Seq((parent1Name.toString() + parent2Name.toString()).replaceAll("\\n","")).flatMap(_.toLowerCase()).map(_.toInt).sum
    val limit = total % getBabyCount()
    SQL"""
         SELECT id, baby_name, baby_date, baby_era_id, created_at
         FROM baby_names
         LIMIT 1 OFFSET $limit
       """.as(babyParser.singleOpt)
  }
  def get(id: Int)(implicit conn: Connection): Option[Baby] = {
    SQL"""
         SELECT id, baby_name, baby_date, baby_era_id, created_at
         FROM baby_names
         WHERE id = $id
       """.as(babyParser.singleOpt)
  }

  def getBabyCount()(implicit conn: Connection): Long = {
    SQL"""
         SELECT COUNT(*) FROM baby_names
       """.as(scalar[Long].single)
  }
}
