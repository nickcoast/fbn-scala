package net.wiringbits.api.models

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import net.wiringbits.common.models.BabyName
import play.api.libs.json.{Format, Json}

object UpdateBaby {

  @ApiModel(value = "UpdateBabyRequest", description = "Request to update baby details")
  case class Request(
    @ApiModelProperty(value = "The baby's name'", dataType = "String", example = "Brozo") name: BabyName,
    @ApiModelProperty(value = "The baby's new name", dataType = "String", example = "Frowup") newName: BabyName,
  )

  @ApiModel(value = "UpdateBabyResponse", description = "Response after updating the baby details")
  case class Response(noData: String = "")

  implicit val updateBabyRequestFormat: Format[Request] = Json.format[Request]
  implicit val updateBabyResponseFormat: Format[Response] = Json.format[Response]
}
