package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult

class EraName private(val string: String) extends WrappedString

object EraName extends WrappedString.Companion[EraName] {

  private val minNameLength: Int = 1 // Could be an era called 'A'

  override def validate(string: String): ValidationResult[EraName] = {
    val isValid = string.length >= minNameLength

    Option
      .when(isValid)(ValidationResult.Valid(string, new EraName(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid Era name")
      }
  }

  override def trusted(string: String): EraName = new EraName(string)
}
