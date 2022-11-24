package net.wiringbits.common.models

import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class EraNameSpec extends AnyWordSpec {

  val valid = List(
    "ale",
    "jo",
    "jorge julian",
    "X",
    ".",
  )

  val invalid = List(
    "",
  )

  "validate" should {
    valid.foreach { input =>
      s"accept valid values: $input" in {
        EraName.validate(input).isValid must be(true)
      }
    }

    invalid.foreach { input =>
      s"reject invalid values: $input" in {
        EraName.validate(input).isValid must be(false)
      }
    }
  }
}
