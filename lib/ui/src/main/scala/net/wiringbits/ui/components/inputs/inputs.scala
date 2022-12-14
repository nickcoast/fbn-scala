package net.wiringbits.ui.components

import net.wiringbits.common.models.{Email, Name, Password, ParentName}
import net.wiringbits.ui.components.core.widgets.ValidatedTextInput

package object inputs {
  object NameInput extends ValidatedTextInput[Name]
  object ParentNameInput extends ValidatedTextInput[ParentName]
  object EmailInput extends ValidatedTextInput[Email]
  object PasswordInput extends ValidatedTextInput[Password]
}
