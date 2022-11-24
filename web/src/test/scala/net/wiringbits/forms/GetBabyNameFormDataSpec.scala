package net.wiringbits.forms

import net.wiringbits.common.models.{Name, ParentName}
import org.scalatest.matchers.must.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class GetBabyNameFormDataSpec extends AnyWordSpec {

  private val initialForm = GetBabyNameFormData.initial(
    parent1NameLabel = "parent_1_name",
    parent2NameLabel = "parent_1_name",
     )

  private val validForm = initialForm
    .copy(
      parent1_name = initialForm.parent1_name.updated(ParentName.validate("someone")),
      parent2_name = initialForm.parent2_name.updated(ParentName.validate("")),
    )

  private val allDataInvalidForm = initialForm
    .copy(
      parent1_name = initialForm.parent1_name.updated(ParentName.validate("")),
      parent2_name = initialForm.parent2_name.updated(ParentName.validate("")), // this is actually valid
    )

  "fields" should {
    "return the expected fields" in {
      val expected = List("parent1_name", "parent2_name")
      initialForm.fields.map(_.name).toSet must be(expected.toSet)
    }
  }

  "formValidationErrors" should {
    "return no errors when everything mandatory is correct" in {
      val result = validForm.formValidationErrors
      result must be(empty)
    }

    "return error when both parent names empty" in {
      // doesn't work because ParentName allows empty. Might need to make separate validators for Parent1 and Parent2 names.
      /*val form = validForm.copy(
        parent1_name = initialForm.parent1_name.updated(ParentName.validate("")),
        parent2_name = initialForm.parent2_name.updated(ParentName.validate(""))
      )*/
      val form = allDataInvalidForm
      form.formValidationErrors must be(List("Please fill out at least first parent name"))
    }

    "return no invalid input when 2nd parent name empty" in { // same as "return no errors when everything mandatory is correct"?
      val form = validForm.copy(
        parent1_name = initialForm.parent1_name.updated(ParentName.validate("Brozo")),
        parent2_name = initialForm.parent2_name.updated(ParentName.validate(""))
      )
      val result = form.formValidationErrors
      result must be(empty)
      //form.formValidationErrors.contains("The passwords does not match") must be(true) // see if error here
    }

    "return all errors" in {
      allDataInvalidForm.formValidationErrors.size must be(1)
    }
  }

  "submitRequest" should {

    "return a request when the data is valid" in {
      val result = validForm.submitRequest
      result.isDefined must be(true)
    }

    /*"return None when the data is not valid" in {
      val form = validForm
      val invalidParent1Name = form.copy(parent1_name = allDataInvalidForm.parent1_name)
      val invalidParent2Name = form.copy(parent2_name = allDataInvalidForm.parent2_name)

      // parent 2 name should allow empty
      List(invalidParent1Name,invalidParent2Name).foreach { form =>
        form.submitRequest.isDefined must be(false)
      }
    }*/
  }
}
