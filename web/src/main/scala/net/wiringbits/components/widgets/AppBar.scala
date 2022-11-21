package net.wiringbits.components.widgets

import com.alexitc.materialui.facade.csstype.csstypeStrings
import com.alexitc.materialui.facade.csstype.mod.{FlexDirectionProperty, TextAlignProperty}
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{CSSProperties, StyleRulesCallback, Styles, WithStylesOptions}
import net.wiringbits.AppContext
import net.wiringbits.core.{I18nHooks, ReactiveHooks}
import net.wiringbits.facades.react.components.image
import net.wiringbits.facades.reactRouterDom.components.Link
import net.wiringbits.models.AuthState
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container.{Alignment, EdgeInsets}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, NavLinkButton, Subtitle, Title}
import net.wiringbits.webapp.utils.slinkyUtils.core.MediaQueryHooks
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}
import slinky.web.html._

@react object AppBar {
  case class Props(ctx: AppContext)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "appbar" -> CSSProperties()
          .setColor("#FFF")
          .setMaxWidth("1200px")
          .setMarginLeft("auto")
          .setMarginRight("auto"),
        "toolbar" -> CSSProperties()
          .setDisplay("flex")
          .setAlignItems("center")
          .setJustifyContent("space-between"),
        "toolbar-mobile" -> CSSProperties()
          .setDisplay("flex")
          .setAlignItems("center")
          .setJustifyContent("flex-start"),
        "menu" -> CSSProperties()
          .setDisplay("flex"),
        "menu-mobile" -> CSSProperties()
          .setDisplay("flex")
          .setFlexDirection(FlexDirectionProperty.column)
          .setColor("#222")
          .setTextAlign(TextAlignProperty.right),
        "logo-image" -> CSSProperties()
          //.setBackgroundImage(url := "/img/FutureBabyNames.jpg")
          .setMaxWidth(1200)
          .setMaxHeight(142)
          .setMarginLeft("auto")
          .setMarginRight("auto")
          .setDisplay("block"),
        "logo-image-mobile" -> CSSProperties()
          //.setBackgroundImage(url := "/img/FutureBabyNames.jpg")
          .setMaxWidth(768)
          .setMaxHeight(100)
          .setMarginLeft("auto")
          .setMarginRight("auto")
          .setDisplay("block")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val texts = I18nHooks.useMessages(props.ctx.$lang)
    val auth = ReactiveHooks.useDistinctValue(props.ctx.$auth)
    val classes = useStyles(())
    val isTablet = MediaQueryHooks.useIsTablet()
    val isMobile = MediaQueryHooks.useIsMobile()
    val (visibleDrawer, setVisibleDrawer) = Hooks.useState(false)

    def onButtonClick(): Unit = {
      if (visibleDrawer) {
        setVisibleDrawer(false)
      }
    }

    val fbnTopImage =
      //a(img(src := "/img/FutureBabyNames.jpg", alt := "FutureBabyNames logo", className := classes("logo-image")), href := "/")
      mui.Link(img(src := "/img/FutureBabyNames.jpg", alt := "FutureBabyNames logo", className := classes("logo-image"))).href("/")
    val fbnTopImageMobile =
      img(src := "/img/FutureBabyNames_400.jpg", alt := "FutureBabyNames logo", className := classes("logo-image"))


    val coreMenu = List(
      NavLinkButton("/", texts.home, onButtonClick),
      //NavLinkButton("/dashboard", texts.dashboard, onButtonClick),
      NavLinkButton("/about", texts.about, onButtonClick),
      NavLinkButton("/drunk-drivers", texts.drunks, onButtonClick),
      NavLinkButton("/kurt-russell-vs-leelee-sobieski", texts.whosBetter, onButtonClick)
    )

    val inauthMenu = List(
      NavLinkButton("/signup", texts.signUp, onButtonClick),
      NavLinkButton("/signin", texts.signIn, onButtonClick)
    )
    val authMenu = List(
      NavLinkButton("/me", texts.profile, onButtonClick),
      NavLinkButton("/signout", texts.signOut, onButtonClick)
    )

    val menu = auth match {
      case AuthState.Authenticated(_) =>
        Fragment(
          coreMenu,
          authMenu
        )

      case AuthState.Unauthenticated =>
        Fragment(
          coreMenu,
          inauthMenu
        )
    }

    if (isMobile || isTablet) {
      val drawerContent = Container(
        minWidth = Some("256px"),
        flex = Some(1),
        margin = EdgeInsets.bottom(32),
        alignItems = Alignment.flexStart,
        //justifyContent = Alignment.spaceBetween,
        child = Fragment(
          mui
            .AppBar(className := classes("appbar"))
            .position(muiStrings.relative)(
              mui.Toolbar(className := classes("toolbar-mobile"))(
                Subtitle(texts.appName)
              )
            ),
          Container(
            alignItems = Alignment.flexEnd,
            justifyContent = Alignment.spaceBetween,
            child = menu
          )
        )
      )

      val drawer = mui.SwipeableDrawer(
        open = visibleDrawer,
        onOpen = _ => setVisibleDrawer(true),
        onClose = _ => setVisibleDrawer(false)
      )(drawerContent)

      val toolbar = mui.Toolbar(className := classes("toolbar-mobile"))(
        mui
          .IconButton(mui.Icon(muiIcons.Menu()))
          .color(Color.inherit)
          .onClick(_ => setVisibleDrawer(true)),
        Subtitle(texts.appName)
      )
      //val mobileOrTabletImg = if(isTablet) "/img/FutureBabyNames_768.jpg" else "/img/FutureBabyNames_425.jpg"
      val mobileOrTabletImg = if(isTablet) "/img/FutureBabyNames_768.jpg" else "/img/FutureBabyNames_425.jpg"

      Fragment(
        div(mui.Link(img(src := mobileOrTabletImg, alt := "FutureBabyNames logo", className := classes("logo-image-mobile"))).href("/")),
        mui
          .AppBar(className := classes("appbar"))
          .position(muiStrings.relative)(toolbar, drawer)
      )
    } else {
      Fragment(
      div(fbnTopImage),
      mui
        .AppBar(className := classes("appbar"))
        .position(muiStrings.relative)(
          mui.Toolbar(className := classes("toolbar"))(
            //Title(texts.appName),
            div(className := classes("menu"))(menu)
          )
        )
      )
    }
  }
}
