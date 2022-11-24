package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.{ParentName, Name}
import play.api.libs.json.{Format, Json}

import java.time.Instant

object GetBaby {

  @ApiModel(value = "GetBabyRequest", description = "Request to get baby from parents")
  case class Request(
    @ApiModelProperty(value = "Parent 1 name'", dataType = "String", example = "Jaimie", required = true)
    parent1Name: ParentName,
    @ApiModelProperty(value = "Parent 2 name", dataType = "String", example = "Alex", required = false, allowEmptyValue = true )
    parent2Name: ParentName,
  )

  @ApiModel(value = "GetBabyResponse", description = "Response after submitting parent names")
  case class Response(
      @ApiModelProperty(value = "Baby name", dataType = "String", example = "Frowup", required = true)
      babyName: Name,
      @ApiModelProperty(value = "The date for the created baby", dataType = "String", example = "2022-10-11T18:06:25.575123Z", required = false, allowEmptyValue = true)
      date: Instant
  )

  implicit val getBabyRequestFormat: Format[Request] = Json.format[Request]
  implicit val getBabyResponseFormat: Format[Response] = Json.format[Response]
}
