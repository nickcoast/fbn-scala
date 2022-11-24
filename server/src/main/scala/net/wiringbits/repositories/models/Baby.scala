package net.wiringbits.repositories.models

import net.wiringbits.common.models.{ParentName, Name}

import java.time.Instant

case class Baby(
  id: Option[Int],
  name: Name,
  date: Instant,
  /*era: Option[Era],*/
  era_id: Option[Int],
  createdAt: Instant,
)

object Baby {
  case class CreateBaby(name: Name, babyDate: Instant)
}
