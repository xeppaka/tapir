package sttp.tapir.server.finatra.cats

import cats.data.NonEmptyList
import cats.effect.std.Dispatcher
import cats.effect.{IO, Resource}
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.finatra.{FinatraRoute, FinatraTestServerInterpreter}
import sttp.tapir.server.tests.TestServerInterpreter
import sttp.tapir.tests.Port

import scala.concurrent.ExecutionContext

class FinatraCatsTestServerInterpreter(dispatcher: Dispatcher[IO])
    extends TestServerInterpreter[IO, Any, FinatraCatsServerOptions[IO], FinatraRoute] {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  override def route(es: List[ServerEndpoint[Any, IO]], interceptors: Interceptors): FinatraRoute = {
    val serverOptions: FinatraCatsServerOptions[IO] = interceptors(FinatraCatsServerOptions.customInterceptors(dispatcher)).options
    val interpreter = FinatraCatsServerInterpreter[IO](serverOptions)
    es.map(interpreter.toRoute).last
  }

  override def server(routes: NonEmptyList[FinatraRoute]): Resource[IO, Port] = FinatraTestServerInterpreter.server(routes)
}
