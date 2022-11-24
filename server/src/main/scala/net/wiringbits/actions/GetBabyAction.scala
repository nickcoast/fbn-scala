package net.wiringbits.actions

import net.wiringbits.api.models.GetBaby
import net.wiringbits.common.models.{ParentName, Name}
import net.wiringbits.repositories.BabiesRepository
import net.wiringbits.repositories.models.Baby

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetBabyAction @Inject()(
    babiesRepository: BabiesRepository
)(implicit ec: ExecutionContext) {
  def apply(parent1Name: ParentName, parent2Name: ParentName): Future[GetBaby.Response] = {
    for {
      baby <- unsafeBaby(parent1Name, parent2Name) // made a baby
    } yield GetBaby.Response(
      babyName = baby.name, // assign baby properties to response
      date = baby.date,
    )
  }

  private def unsafeBaby(parent1Name: ParentName, parent2Name: ParentName): Future[Baby] = {
    babiesRepository
      .getBaby(parent1Name, parent2Name)
      .map { maybe =>
        maybe.getOrElse(
          throw new RuntimeException(
            s"Unexpected error because the user wasn't found for parent1 '$parent1Name' and parent2 '$parent2Name'"
          )
        )
      }
  }
}
