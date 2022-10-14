package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.AppContext
import net.wiringbits.common.ErrorMessages
import net.wiringbits.core.I18nHooks
import net.wiringbits.forms.GetBabyNameFormData
import net.wiringbits.models.User
import net.wiringbits.ui.components.inputs.NameInput
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.webapp.utils.slinkyUtils.forms.StatefulFormData
import org.scalajs.dom
import org.scalajs.dom.window.alert
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks, ReactElement}
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.web.html._
import typings.reactRouterDom.{mod => reactRouterDom}

import scala.util.{Failure, Success}

// TODO: delete this. for testing only
import org.scalajs.dom

@react object GetBabyNameForm {
  case class Props(ctx: AppContext)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = reactRouterDom.useHistory()
    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        GetBabyNameFormData.initial(
          parent1NameLabel = texts.babyParent1,
          parent2NameLabel = texts.babyParent2
        )
      )
    )

    def onDataChanged(f: GetBabyNameFormData => GetBabyNameFormData): Unit = {
      setFormData { current =>
        current.filling.copy(data = f(current.data))
      }
    }

    def handleSubmit(e: SyntheticEvent[_, dom.Event]): Unit = {
      e.preventDefault()

      if (formData.isSubmitButtonEnabled) {
        setFormData(_.submit)
        for {
          request <- formData.data.submitRequest
            .orElse {
              setFormData(_.submissionFailed(texts.completeData))
              None
            }
        } yield props.ctx.api.client
          .getBaby(request)
          .onComplete {
            case Success(res) =>
              setFormData(_.submitted)
              //props.ctx.baby
              dom.window.alert(res.babyName.string)
              //props.ctx.loggedIn(User(res.name, res.email))
              //history.push("/dashboard") // redirects to the dashboard

            case Failure(ex) =>
              setFormData(_.submissionFailed(ex.getMessage))
          }
      } else {
        println("Submit fired when it is not available")
      }
    }

    val parent1Input = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(8),
      child = NameInput
        .component(
          NameInput.Props(
            formData.data.parent1_name,
            disabled = formData.isInputDisabled,
            onChange = value => onDataChanged(x => x.copy(parent1_name = x.parent1_name.updated(value)))
          )
        )
    )

    val parent2Input = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(16),
      child = NameInput
        .component(
          NameInput.Props(
            formData.data.parent2_name,
            disabled = formData.isInputDisabled,
            onChange = value => onDataChanged(x => x.copy(parent2_name = x.parent2_name.updated(value)))
          )
        )
    )

    /*def getBabyNameButton(text: String): ReactElement = {
      // TODO: It would be ideal to match the error against a code than matching a text
      text match {
        case ErrorMessages.`emailNotVerified` =>
          val email = formData.data.email.inputValue

          mui
            .Button(texts.resendEmail)
            .variant(muiStrings.text)
            .color(muiStrings.primary)
            .onClick(_ => history.push(s"/resend-verify-email?email=${email}"))
        case _ => Fragment()
      }
    }*/

    val error = formData.firstValidationError.map { errorMessage =>
      Container(
        alignItems = Alignment.center,
        margin = Container.EdgeInsets.top(16),
        child = Fragment(
          ErrorLabel(errorMessage)
        )
      )
    }

    val getBabyNameButton = {
      val text =
        if (formData.isSubmitting)
          Fragment(
            CircularLoader(),
            Container(margin = EdgeInsets.left(8), child = texts.loading)
          )
        else
          Fragment(texts.babySubmit)

      mui
        .Button(text)
        .fullWidth(true)
        .disabled(formData.isSubmitButtonDisabled)
        .variant(muiStrings.contained)
        .color(Color.primary)
        .`type`(muiStrings.submit)
    }

    form(
      onSubmit := (handleSubmit(_))
    )(
      Container(
        alignItems = Alignment.center,
        justifyContent = Alignment.center,
        child = Fragment(
          parent1Input,
          parent2Input,
          error,
          Container(
            minWidth = Some("100%"),
            margin = EdgeInsets.top(16),
            alignItems = Alignment.center,
            child = getBabyNameButton
          )
        )
      )
    )
  }
}
