package net.wiringbits.components.widgets

import net.wiringbits.AppContext
import net.wiringbits.api.models.GetUserLogs
import net.wiringbits.components.FutureComponent
import net.wiringbits.components.FutureComponent.State
import net.wiringbits.models.User
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ConditionalComponent
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.Container
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, ReactElement}

@react object Logs {
  case class Props(ctx: AppContext, user: User)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    def loadingLogs(): ReactElement = {
      Container(
        flex = Some(1),
        alignItems = Container.Alignment.center,
        justifyContent = Container.Alignment.center,
        child = Loader(props.ctx)
      )
    }

    def refreshButton(onClick: () => Unit) = mui
      .Button("Refresh")
      .variant(muiStrings.contained)
      .color(muiStrings.primary)
      .onClick(_ => onClick())

    def loadedLogs(response: GetUserLogs.Response, refresh: () => Unit): ReactElement = {
      println(response)
      ConditionalComponent(response.data.isEmpty)(
        emptyLogs(refresh),
        LogList(props.ctx, response, refresh)
      )
    }

    def emptyLogs(refresh: () => Unit): ReactElement = {
      Container(
        flex = Some(1),
        alignItems = Container.Alignment.center,
        justifyContent = Container.Alignment.center,
        child = Fragment(
          mui.Typography("Logs are empty").variant(muiStrings.h5),
          refreshButton(refresh)
        )
      )
    }

    def errorLog(error: Throwable, refresh: () => Unit): ReactElement = {
      println(error)
      Container(
        flex = Some(1),
        alignItems = Container.Alignment.center,
        justifyContent = Container.Alignment.center,
        child = Fragment(
          mui.Typography("An error has occurred while fetching the logs"),
          refreshButton(refresh)
        )
      )
    }

    FutureComponent[GetUserLogs.Response](
      fetch = () => props.ctx.api.client.getUserLogs(),
      render = {
        case State.Loading() => loadingLogs()
        case State.Loaded(response, refresh) => loadedLogs(response, refresh)
        case State.Failed(error, refresh) => errorLog(error, refresh)
      }
    )
  }
}
