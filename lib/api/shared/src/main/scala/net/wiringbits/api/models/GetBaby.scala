package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.{BabyName, Name}
import play.api.libs.json.{Format, Json}

import java.time.Instant

object GetBaby {

  @ApiModel(value = "GetBabyRequest", description = "Request to get baby from parents")
  case class Request(
    @ApiModelProperty(value = "Parent 1 name'", dataType = "String", example = "Jaimie")
    parent1Name: Name,
    @ApiModelProperty(value = "Parent 2 name", dataType = "String", example = "Alex")
    parent2Name: Name,
  )

  @ApiModel(value = "GetBabyResponse", description = "Response after submitting parent names")
  case class Response(
      @ApiModelProperty(value = "Baby name", dataType = "String", example = "Frowup")
      babyName: BabyName,
      @ApiModelProperty(value = "The date for the created baby", dataType = "String", example = "2022-10-11T18:06:25.575123Z")
      date: Instant
  )

  implicit val getBabyRequestFormat: Format[Request] = Json.format[Request]
  implicit val getBabyResponseFormat: Format[Response] = Json.format[Response]
}
