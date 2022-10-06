package net.wiringbits.repositories.models

import net.wiringbits.common.models.{BabyName}

import java.time.Instant

case class Baby(
  id: Option[Int],
  name: BabyName,
  date: Instant,
  /*era: Option[Era],*/
    era_id: Option[Int],
  createdAt: Instant,
)

object Baby {
  case class CreateBaby(name: BabyName, babyDate: Instant)
}
