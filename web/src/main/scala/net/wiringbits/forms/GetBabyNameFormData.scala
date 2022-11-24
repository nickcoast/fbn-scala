package net.wiringbits.forms

import net.wiringbits.api.models.{GetBaby, CreateBaby}
import net.wiringbits.common.models.{ParentName}
import net.wiringbits.webapp.utils.slinkyUtils.forms.{FormData, FormField}

case class GetBabyNameFormData(
    parent1_name: FormField[ParentName],
    parent2_name: FormField[ParentName],
) extends FormData[GetBaby.Request] {
  override def fields: List[FormField[_]] = List(parent1_name, parent2_name)

  override def formValidationErrors: List[String] = {
    List(
      validateParentNamesNotBothEmpty,
      fieldsError,
    ).flatten
  }

  protected def validateParentNamesNotBothEmpty: Option[String] = {
    val bothEmpty = parent1_name.inputValue.length + parent2_name.inputValue.length == 0
    if(bothEmpty) Some("Please fill out at least first parent name")
    else None
  }

  override def submitRequest: Option[GetBaby.Request] = {
    val formData = this
    for {
      parent1_name <- formData.parent1_name.valueOpt
      parent2_name <- formData.parent2_name.valueOpt
    } yield GetBaby.Request(
      parent1_name,
      parent2_name,
    )
  }
}

object GetBabyNameFormData {
  def initial(
      parent1NameLabel: String,
      parent2NameLabel: String,
  ): GetBabyNameFormData = GetBabyNameFormData(
    parent1_name = new FormField(label = parent1NameLabel, name = "parent1_name", required = true),
    parent2_name = new FormField(label = parent2NameLabel, name = "parent2_name", required = false, value = Some(ParentName.validate(""))),
  )
}
