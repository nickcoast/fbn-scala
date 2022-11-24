package net.wiringbits.components.pages

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{typographyTypographyMod, components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}
import net.wiringbits.AppContext
import net.wiringbits.core.I18nHooks
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.web.html._

@react object DrunkDrivers {
  case class Props(ctx: AppContext)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "drunkImage" -> CSSProperties()
          .setMaxWidth(1000)
          .setMaxHeight(752)
          .setWidth("100%")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val classes = useStyles(())

    /*
    val wiringbitsImage =
      img(src := "/img/wiringbits-logo.png", alt := "wiringbits logo", className := classes("image"))
      */

    val fbnDescriptionText = Fragment(
      p("Think drunk driving is fun, kids? Well look at the picture below and then tell me what you think! While you're at it, try clicking the picture for a special bonus!"),
    )


    val drunkImage = {
      a(href := "https://btousey.files.wordpress.com/2010/07/kurt-russell-josh-sacco-aka-rizzo.jpg?w=900&h=775",
      img(src := "/img/home/drunk_drivers.jpg", alt := "Drunk Drivers", className := classes("drunkImage")))
    }

    Container(
      flex = Some(1),
      alignItems = Alignment.center,
      margin = EdgeInsets.top(48),
      child = Fragment(
        fbnDescriptionText,
        drunkImage,
        //wiringbitsImage,
        /*Container(
          margin = EdgeInsets.top(888),
          child = repositoryLink
        )*/
      )
    )
  }
}
