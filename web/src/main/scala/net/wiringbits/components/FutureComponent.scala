package net.wiringbits.components

import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.core.facade.{Hooks, ReactElement}
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object FutureComponent {

  sealed trait State[D] extends Product with Serializable
  object State {
    case class Loading[D]() extends State[D]
    case class Loaded[D](response: D, refresh: () => Unit) extends State[D]
    case class Failed[D](error: Throwable, refresh: () => Unit) extends State[D]
  }

  case class Props[D](future: () => Future[D], render: State[D] => ReactElement)

  def component[D]: FunctionalComponent[Props[D]] = FunctionalComponent[Props[D]] { props =>
    val (state, setState) = Hooks.useState[State[D]](State.Loading[D]())
    def callFuture(): Unit = {
      setState(State.Loading[D]())
      props.future().onComplete {
        case Success(value) => setState(State.Loaded(value, callFuture))
        case Failure(error) => setState(State.Failed[D](error, callFuture))
      }
    }
    Hooks.useEffect(callFuture, List.empty)
    props.render(state)
  }

  def apply[D](fetch: () => Future[D], render: State[D] => ReactElement): KeyAddingStage = component(
    Props(fetch, render)
  )
}
