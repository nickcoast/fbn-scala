package net.wiringbits.api.models

import io.swagger.annotations._
import net.wiringbits.common.models.EraName
import play.api.libs.json.{Format, Json}

import java.time.Instant


object CreateEra {
  @ApiModel(value = "EraCreateRequest", description = "Request for the create era API")
  case class Request( // seems like property count/type (not name) must match companion object of the repo model?
    @ApiModelProperty(value = "The era's name", dataType = "String", example = "Post Apocalyptic Hellscape II")
    name: EraName, // Era has name, date. user has Email, Password, Captcha.
    @ApiModelProperty(value = "The era's start date", dataType = "Instant", example = "31556889864403199L")
    startDate: Instant, // right type??
  )

  @ApiModel(value = "EraCreateResponse", description = "Response for the create era API")
  case class Response(
    @ApiModelProperty(
     value = "The id for the created era",
     dataType = "Int",
     example = "1"
    )
    id: Int,
    @ApiModelProperty(value = "The name for the created era", dataType = "String", example = "email@wiringbits.net")
    name: EraName,
    @ApiModelProperty(value = "The email for the created era", dataType = "String", example = "Alex")
    date: Instant
  )

  implicit val createEraRequestFormat: Format[Request] = Json.format[Request]
  implicit val createEraResponseFormat: Format[Response] = Json.format[Response]
}
