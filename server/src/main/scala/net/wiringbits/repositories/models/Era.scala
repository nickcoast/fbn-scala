package net.wiringbits.repositories.models

import net.wiringbits.common.models.Name

import java.time.Instant

case class Era(
    id: Option[Int],
    name: Name,
    story: Option[String],
    startDate: Instant,
    endDate: Option[Instant], // may not know the end!
)

object Era {
  case class CreateEra(name: Name, startDate: Instant)
}
