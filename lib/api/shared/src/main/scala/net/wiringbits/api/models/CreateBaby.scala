package net.wiringbits.api.models

import io.swagger.annotations._
import net.wiringbits.common.models.{ParentName,Name}
import play.api.libs.json.{Format, Json}
import java.time.Instant


object CreateBaby {
  @ApiModel(value = "CreateBabyRequest", description = "Request for the create baby API")
  case class Request( // seems like property count/type (not name) must match companion object of the repo model?
      @ApiModelProperty(value = "The baby's name", dataType = "String", example = "Alex")
      name: Name, // Baby has name, date. user has Email, Password, Captcha.
      @ApiModelProperty(value = "The baby's future date", dataType = "Instant", example = "2022-10-11T18:06:25.575123Z")
      date: Instant, // right type??
  )
  @ApiModel(value = "CreateBabyResponse", description = "Response for the create baby API")
  case class Response(
    /*  @ApiModelProperty(
        value = "The id for the created baby",
        dataType = "Int",
        example = "1"
      )
      id: Int,*/
      @ApiModelProperty(value = "The name for the created baby", dataType = "String", example = "Brozo")
      name: Name,
      @ApiModelProperty(value = "The date for the created baby", dataType = "String", example = "2022-10-11T18:06:25.575123Z")
      date: Instant
  )

  implicit val createBabyRequestFormat: Format[Request] = Json.format[Request]
  implicit val createBabyResponseFormat: Format[Response] = Json.format[Response]
}
