package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult

class ParentName private(val string: String) extends WrappedString

object ParentName extends WrappedString.Companion[ParentName] {

  private val minNameLength: Int = 0 // Baby could be named 'X'

  override def validate(string: String): ValidationResult[ParentName] = {
    val isValid = string.length >= minNameLength

    Option
      .when(isValid)(ValidationResult.Valid(string, new ParentName(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid Baby name")
      }
  }

  override def trusted(string: String): ParentName = new ParentName(string)
}
