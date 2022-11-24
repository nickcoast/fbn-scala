package net.wiringbits.actions

import net.wiringbits.api.models.UpdateBaby
import net.wiringbits.common.models.Name
import net.wiringbits.repositories.BabiesRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import java.time.Instant

class UpdateBabyAction @Inject()(
    babiesRepository: BabiesRepository
)(implicit ec: ExecutionContext) {

  def apply(babyName: Name, request: UpdateBaby.Request): Future[Unit] = {
    val validate = Future {
      if (request.newName.string.isEmpty) new RuntimeException(s"The name is required")
      else ()
    }

    for {
      _ <- validate
      _ <- babiesRepository.update(babyName, request.newName)
    } yield ()
  }
}
