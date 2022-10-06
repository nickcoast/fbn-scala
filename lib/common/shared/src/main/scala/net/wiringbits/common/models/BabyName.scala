package net.wiringbits.common.models

import net.wiringbits.webapp.common.models.WrappedString
import net.wiringbits.webapp.common.validators.ValidationResult

class BabyName private(val string: String) extends WrappedString

object BabyName extends WrappedString.Companion[BabyName] {

  private val minNameLength: Int = 1 // Baby could be named 'X'

  override def validate(string: String): ValidationResult[BabyName] = {
    val isValid = string.length >= minNameLength

    Option
      .when(isValid)(ValidationResult.Valid(string, new BabyName(string)))
      .getOrElse {
        ValidationResult.Invalid(string, "Invalid Baby name")
      }
  }

  override def trusted(string: String): BabyName = new BabyName(string)
}
