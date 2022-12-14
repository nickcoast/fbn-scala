package net.wiringbits.validations

import net.wiringbits.common.models.Name
import net.wiringbits.repositories.BabiesRepository

import scala.concurrent.{ExecutionContext, Future}

object ValidateBabyNameIsAvailable {
  def apply(repository: BabiesRepository, name: Name)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      maybe <- repository.find(name)
    } yield {
      if (maybe.isDefined) throw new RuntimeException(s"That Baby Name already exists")
      else ()
    }
  }
}
