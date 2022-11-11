package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.materialUiCore.components.Dialog
import com.alexitc.materialui.facade.materialUiCore.materialUiCoreStrings.classes
import com.alexitc.materialui.facade.materialUiCore.mod.{DialogTitle, PropTypes}
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}

//dialog-related
import slinky.core.{Component => CoreComponent }
/*import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles*/
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import org.scalablytyped.runtime.StringDictionary
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.csstype.mod.PositionProperty
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.csstype.csstypeStrings.auto
// end dialog-related
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
import org.scalajs.dom.document // for alert in testing
import org.scalajs.dom.window.alert
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks, ReactElement}
import slinky.core.{FunctionalComponent, SyntheticEvent, WithAttrs}
import slinky.web.html.{div, _}
import typings.reactRouterDom.{mod => reactRouterDom}

import scala.util.{Failure, Success}

// TODO: delete this. for testing only
import org.scalajs.dom

@react object GetBabyNameForm {
  case class Props(ctx: AppContext)

  // from https://github.com/wiringbits/cazadescuentos/blob/1df397f90d3159c50e19859f54ef472af8659b05/pwa/src/main/scala/net/wiringbits/cazadescuentos/components/AddNewItemFloatingButton.scala#L109-L131
  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "fab" -> CSSProperties()
          .setPosition(PositionProperty.fixed)
          .setWidth(64)
          .setHeight(64)
          .setBottom(88)
          .setRight("calc(50vw - 32px)"),
        "root" -> CSSProperties().setWidth("100%").setOverflowX(auto)
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }


  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val history = reactRouterDom.useHistory()
    val (formData, setFormData) = Hooks.useState(
      StatefulFormData(
        GetBabyNameFormData.initial(
          parent1NameLabel = texts.babyParent1,
          parent2NameLabel = texts.babyParent2,
        )
      )
    )

    val (babyData, setBabyData) = Hooks.useState("")
    val (dialogOpened, setDialogOpened) = Hooks.useState(false)
    /*@react object BabyNameDialog {
      case class Props(ctx: AppContext)
      val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
        val (babyData, setBabyData) = Hooks.useState(
          Container(
            Dialog(true)
          )
        )
      }
    }*/

    def handleDialogClosed(): Unit = setDialogOpened(false)

    val showBabyData = Container(
      minWidth = Some("100%"),
      margin = EdgeInsets.bottom(8),
      child = Dialog(open = true)
        .onClose(_ => handleDialogClosed()
      )
    )
    /*@react class BabyNameDialog extends CoreComponent {
      case class Props(ctx: AppContext)
      case class State(babyName: String)

      def initialState = State("")

      def setBabyName(babyName: String): Unit = {
        setState(State(babyName))
      }

      def render = {
        div(
          h1(state.babyName)
        )
      }
    }*/

    def onDataChanged(f: GetBabyNameFormData => GetBabyNameFormData): Unit = {
      setFormData { current =>
        current.filling.copy(data = f(current.data))
      }
    }

    /*val babyBox = Container(
      child =
        p("Your baby name will appear here")

    )*/

    // Seee Using the DOM Library at https://www.scala-js.org/doc/tutorial/basic/index.html
    def appendBabyBox(targetNode: dom.Node, text: String): Unit = {
      val babyBox = document.createElement("div")
      babyBox.textContent = text
      targetNode.appendChild(babyBox)
    }


    //var babyBox = Dialog(true)
    val (babyBoxOpened, setBabyBoxOpened) = Hooks.useState(false)
    def closeBabyBox(): Unit = setBabyBoxOpened(false)
    def openBabyBox(): Unit = setBabyBoxOpened(true)

    //var babyBox: WithAttrs[div.tag.type] = div(p("LOLOL"))
    val babyBox = Container(
      //<div style=""><p>"HAHAHAHA!"</p></div>
      p("Hahaha!!!! Yeah! What??")
    )

    def getBabyBox(babyName: String): WithAttrs[div.tag.type] = {
      /*.title("Getcher babyname here")*/

      /*div(
        Dialog(true)
          .`aria-labelledby`("alert-baby-name-title")
          .`aria-describedby`("alert-baby-name-description")
        mui.DialogTitle
      )
      */
      div(
      Dialog(true)
        .onClose(_ => closeBabyBox())
        (
          mui.DialogTitle("Welcome your future baby!"),
          mui.DialogContent(
            mui.DialogContentText(
              babyName
            )
              .id("alert-baby-name-description")

          ),
         /*
         // this works but no text field needed here now
          mui.TextField
            .StandardTextFieldProps()
            .autoFocus(true)
            .margin(PropTypes.Margin.dense)
            .label("asdf")
            .fullWidth(true),*/
          mui.DialogActions(
            div(mui.Button("close").color(PropTypes.Color.primary).onClick(_ => closeBabyBox()))
          )

            /*mui
        .Button(text)
        .fullWidth(true)
        .disabled(formData.isSubmitButtonDisabled)
        .variant(muiStrings.contained)
        .color(Color.primary)
        .`type`(muiStrings.submit)*/

        )
      )
        /*.contentEditable(true)
        .fullWidth(true)
        .draggable(true)*/
        //.className("jimmy")
        //.args()
    }
    /*def getBabyBox(targetNode: dom.Node, text: String): Unit = {

    }*/

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
              appendBabyBox(document.body,res.babyName.string) // works appends div to end of page
              //babyBox = getBabyBox(res.babyName.string)
              setBabyData(res.babyName.string)
              //babyBox.setBabyName(res.babyName.string)
              //babyBox = getBabyBox(res.babyName.string)
              //props.ctx.baby
              //dom.window.alert(res.babyName.string)
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
          ),
          Container(
            child = getBabyBox("Jerkel")
          )
          /*Container(
            child = Fragment(

              Container(margin = EdgeInsets.left(8), child = texts.loading)
            )
          )*/

        )
      )
    )
  }
}
