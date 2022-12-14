package net.wiringbits.api

import net.wiringbits.api.models._
import play.api.libs.json._
import sttp.client3._
import sttp.model._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait ApiClient {
  def createUser(request: CreateUser.Request): Future[CreateUser.Response]
  def createBaby(request: CreateBaby.Request): Future[CreateBaby.Response]
  def updateBaby(request: UpdateBaby.Request): Future[UpdateBaby.Response]
  def getBaby(request: GetBaby.Request): Future[GetBaby.Response]
  def login(request: Login.Request): Future[Login.Response]
  def logout(): Future[Logout.Response]

  def verifyEmail(request: VerifyEmail.Request): Future[VerifyEmail.Response]
  def forgotPassword(request: ForgotPassword.Request): Future[ForgotPassword.Response]
  def resetPassword(request: ResetPassword.Request): Future[ResetPassword.Response]

  def currentUser(): Future[GetCurrentUser.Response]
  def updateUser(request: UpdateUser.Request): Future[UpdateUser.Response]
  def updatePassword(request: UpdatePassword.Request): Future[UpdatePassword.Response]
  def getUserLogs(): Future[GetUserLogs.Response]

  def adminGetUserLogs(userId: UUID): Future[AdminGetUserLogs.Response]
  def adminGetUsers(): Future[AdminGetUsers.Response]

  def getEnvironmentConfig(): Future[GetEnvironmentConfig.Response]

  def sendEmailVerificationToken(
      request: SendEmailVerificationToken.Request
  ): Future[SendEmailVerificationToken.Response]
}

object ApiClient {
  case class Config(serverUrl: String)

  private def asJson[R: Reads] = {
    asString
      .map {
        case Right(response) =>
          // handles 2xx responses
          Success(response)
        case Left(response) =>
          // handles non 2xx responses
          Try {
            val json = Json.parse(response)
            // TODO: Unify responses to match the play error format
            json
              .asOpt[ErrorResponse]
              .orElse {
                json
                  .asOpt[PlayErrorResponse]
                  .map(model => ErrorResponse(model.error.message))
              }
              .getOrElse(throw new RuntimeException(s"Unexpected JSON response: $response"))
          } match {
            case Failure(exception) =>
              println(s"Unexpected response: ${exception.getMessage}")
              exception.printStackTrace()
              Failure(new RuntimeException(s"Unexpected response, please try again in a minute"))
            case Success(value) =>
              Failure(new RuntimeException(value.error))
          }
      }
      .map { t =>
        t.map(Json.parse).map(_.as[R])
      }
  }

  // TODO: X-Authorization header is being used to keep the nginx basic-authentication
  // once that's removed, Authorization header can be used instead.
  class DefaultImpl(config: Config)(implicit
      backend: SttpBackend[Future, _],
      ec: ExecutionContext
  ) extends ApiClient {

    private val ServerAPI = sttp.model.Uri
      .parse(config.serverUrl)
      .getOrElse(throw new RuntimeException("Invalid server url"))

    /** This is necessary for non-browser clients, this way, the cookies from the last authentication response are
      * propagated to the next requests
      */
    private var lastAuthResponse = Option.empty[Response[_]]

    private def unsafeSetLoginResponse(response: Response[_]): Unit = synchronized {
      lastAuthResponse = Some(response)
    }

    private def prepareRequest[R: Reads] = {
      val base = basicRequest
        .contentType(MediaType.ApplicationJson)
        .response(asJson[R])

      lastAuthResponse
        .map(base.cookies)
        .getOrElse(base)
    }

    override def createUser(request: CreateUser.Request): Future[CreateUser.Response] = {
      val path = ServerAPI.path :+ "users"
      val uri = ServerAPI.withPath(path)

      prepareRequest[CreateUser.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def createBaby(request: CreateBaby.Request): Future[CreateBaby.Response] = {
      val path = ServerAPI.path :+ "babies"
      val uri = ServerAPI.withPath(path)

      prepareRequest[CreateBaby.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def updateBaby(request: UpdateBaby.Request): Future[UpdateBaby.Response] = {
      val path = ServerAPI.path :+ "babies" :+ "baby"
      val uri = ServerAPI.withPath(path)

      prepareRequest[UpdateBaby.Response]
        .put(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def getBaby(request: GetBaby.Request): Future[GetBaby.Response] = {
      // TODO: look into why changing return type to wrong type e.g. Future[UpdateBaby.Response]
      // leads to error in Future.fromTry
      val path = ServerAPI.path :+ "babies" :+ "name"
      val uri = ServerAPI.withPath(path)

      prepareRequest[GetBaby.Response]
        .put(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def verifyEmail(request: VerifyEmail.Request): Future[VerifyEmail.Response] = {
      val path = ServerAPI.path :+ "users" :+ "verify-email"
      val uri = ServerAPI.withPath(path)

      prepareRequest[VerifyEmail.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def forgotPassword(request: ForgotPassword.Request): Future[ForgotPassword.Response] = {
      val path = ServerAPI.path :+ "users" :+ "forgot-password"
      val uri = ServerAPI.withPath(path)

      prepareRequest[ForgotPassword.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def resetPassword(request: ResetPassword.Request): Future[ResetPassword.Response] = {
      val path = ServerAPI.path :+ "users" :+ "reset-password"
      val uri = ServerAPI.withPath(path)

      prepareRequest[ResetPassword.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def login(request: Login.Request): Future[Login.Response] = {
      val path = ServerAPI.path :+ "auth" :+ "login"
      val uri = ServerAPI.withPath(path)

      prepareRequest[Login.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map { response =>
          // non-browser clients require the auth cookie to be set manually, hence, we need to store it
          unsafeSetLoginResponse(response)
          response.body
        }
        .flatMap(Future.fromTry)
    }

    override def logout(): Future[Logout.Response] = {
      val path = ServerAPI.path :+ "auth" :+ "logout"
      val uri = ServerAPI.withPath(path)

      prepareRequest[Logout.Response]
        .post(uri)
        .body(Json.toJson(Logout.Request()).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def currentUser(): Future[GetCurrentUser.Response] = {
      val path = ServerAPI.path :+ "auth" :+ "me"
      val uri = ServerAPI.withPath(path)

      prepareRequest[GetCurrentUser.Response]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def updateUser(request: UpdateUser.Request): Future[UpdateUser.Response] = {
      val path = ServerAPI.path :+ "users" :+ "me"
      val uri = ServerAPI.withPath(path)

      prepareRequest[UpdateUser.Response]
        .put(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def updatePassword(request: UpdatePassword.Request): Future[UpdatePassword.Response] = {
      val path = ServerAPI.path :+ "users" :+ "me" :+ "password"
      val uri = ServerAPI.withPath(path)

      prepareRequest[UpdatePassword.Response]
        .put(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def getUserLogs(): Future[GetUserLogs.Response] = {
      val path = ServerAPI.path :+ "users" :+ "me" :+ "logs"
      val uri = ServerAPI.withPath(path)

      prepareRequest[GetUserLogs.Response]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminGetUserLogs(userId: UUID): Future[AdminGetUserLogs.Response] = {
      val path = ServerAPI.path :+ "admin" :+ "users" :+ userId.toString :+ "logs"
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminGetUserLogs.Response]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminGetUsers(): Future[AdminGetUsers.Response] = {
      val path = ServerAPI.path :+ "admin" :+ "users"
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminGetUsers.Response]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def getEnvironmentConfig(): Future[GetEnvironmentConfig.Response] = {
      val path = ServerAPI.path :+ "environment-config"
      val uri = ServerAPI.withPath(path)

      prepareRequest[GetEnvironmentConfig.Response]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def sendEmailVerificationToken(
        request: SendEmailVerificationToken.Request
    ): Future[SendEmailVerificationToken.Response] = {
      val path = ServerAPI.path :+ "users" :+ "email-verification-token"
      val uri = ServerAPI.withPath(path)

      prepareRequest[SendEmailVerificationToken.Response]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }
  }
}
