package net.wiringbits.actions

import net.wiringbits.api.models.CreateBaby
import net.wiringbits.apis.ReCaptchaApi
import net.wiringbits.config.UserTokensConfig
import net.wiringbits.repositories
import net.wiringbits.repositories.{BabiesRepository, UsersRepository}
import net.wiringbits.util.{EmailsHelper, TokenGenerator, TokensHelper}
import net.wiringbits.validations.{ValidateBabyNameIsAvailable}
import org.mindrot.jbcrypt.BCrypt

import java.time.Instant
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateBabyAction @Inject()(
    babiesRepository: BabiesRepository,
    usersRepository: UsersRepository, // maybe need this here, maybe not
    reCaptchaApi: ReCaptchaApi,
    tokenGenerator: TokenGenerator,
    userTokensConfig: UserTokensConfig,
    emailsHelper: EmailsHelper
)(implicit
    ec: ExecutionContext
) {

  def apply(request: CreateBaby.Request): Future[CreateBaby.Response] = {
    for {
      _ <- validations(request)
      // create the baby
      createBaby = repositories.models.Baby
        .CreateBaby(
          name = request.name,
          babyDate = request.date,
        )
      _ <- babiesRepository.create(createBaby)

    } yield CreateBaby.Response(name = createBaby.name, date = createBaby.babyDate)
  }

  private def validations(request: CreateBaby.Request) = {
    for {
      _ <- ValidateBabyNameIsAvailable(babiesRepository, request.name)
    } yield ()
  }
}
