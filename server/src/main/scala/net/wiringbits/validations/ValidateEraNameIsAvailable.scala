package net.wiringbits.validations

import net.wiringbits.common.models.EraName
import net.wiringbits.repositories.ErasRepository

import scala.concurrent.{ExecutionContext, Future}
import java.time.Instant

object ValidateEraNameIsAvailable {
  def apply(repository: ErasRepository, name: EraName)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      maybe <- repository.find(name)
    } yield {
      if (maybe.isDefined) throw new RuntimeException(s"That Era Name already exists")
      else ()
    }
  }
}
