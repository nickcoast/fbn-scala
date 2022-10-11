package controllers

import io.swagger.annotations._
import net.wiringbits.actions._
import net.wiringbits.api.models._
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import java.time.Instant

import javax.inject.Inject
import scala.concurrent.ExecutionContext

/*@SwaggerDefinition(
  securityDefinition = new SecurityDefinition(
    apiKeyAuthDefinitions = Array(
      new ApiKeyAuthDefinition(
        name = "Cookie",
        key = "auth_cookie",
        in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER,
        description =
          "The baby's session cookie retrieved when logging into the app, invoke the login API to get the cookie stored in the browser"
      )
    )
  )
)*/
@Api("Babies")
class BabiesController @Inject()(
    createBabyAction: CreateBabyAction,
    updateBabyAction: UpdateBabyAction,
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  //private val logger = LoggerFactory.getLogger(this.getClass)

/*  @ApiOperation(
    value = "Creates a new baby",
    notes = "Hi"
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[CreateBaby.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The baby was created", response = classOf[CreateBaby.Response]),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )*/
  def create() = handleJsonBody[CreateBaby.Request] { request =>
    val body = request.body
    //logger.info(s"Create baby: $body")
    for {
      response <- createBabyAction(body)
    } yield Ok(Json.toJson(response))
  }

  /*@ApiOperation(
    value = "Updates the baby details",
    authorizations = Array(new Authorization(value = "auth_cookie"))
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[UpdateBaby.Request]
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(
        code = 200,
        message = "The baby details were updated",
        response = classOf[UpdateBaby.Response]
      ),
      new ApiResponse(code = 400, message = "Invalid or missing arguments")
    )
  )*/
  def update() = handleJsonBody[UpdateBaby.Request] { request =>
    val body = request.body
    //logger.info(s"Update baby: $body")
    for {
      _ <- updateBabyAction(request.body.name, body)
      response = UpdateBaby.Response()
    } yield Ok(Json.toJson(response))
  }
}
