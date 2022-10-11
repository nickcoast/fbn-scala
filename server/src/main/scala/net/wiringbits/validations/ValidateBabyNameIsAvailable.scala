package net.wiringbits.validations

import net.wiringbits.common.models.BabyName
import net.wiringbits.repositories.BabiesRepository

import scala.concurrent.{ExecutionContext, Future}
import java.time.Instant

object ValidateBabyNameIsAvailable {
  def apply(repository: BabiesRepository, name: BabyName)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      maybe <- repository.find(name)
    } yield {
      if (maybe.isDefined) throw new RuntimeException(s"That Baby Name already exists")
      else ()
    }
  }
}
