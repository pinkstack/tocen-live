package com.pinkstack.tocenlive

import cats.effect.{Async, Resource, Temporal}
import cats.implicits._
import fs2.concurrent.Topic
import fs2.{Pipe, Stream}
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.Text
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.DurationInt

final case class WebServer[F[_]: Async: Logger](changesTopic: Topic[F, Change])(
    implicit F: Temporal[F]) {
  import io.circe.generic.auto._
  import io.circe.syntax._
  import org.http4s.dsl.io._

  def wsRoutes(wsb: WebSocketBuilder[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "ws" / "changes" =>
      val toClient: Stream[F, WebSocketFrame.Text] =
        changesTopic
          .subscribe(100)
          .delayBy(50.milliseconds)
          .map { change =>
            Text(Event(change.getClass.getSimpleName, change).asJson.noSpaces)
          }

      val fromClient: Pipe[F, WebSocketFrame, Unit] = _.void
      wsb.build(toClient, fromClient)
  }

  def mkResource(): Resource[F, org.http4s.server.Server] =
    BlazeServerBuilder[F]
      .withBanner(Seq.empty)
      .bindHttp(8077, "0.0.0.0")
      .withHttpWebSocketApp(wsRoutes(_).orNotFound)
      .resource
      .evalTap(_ => Logger[F].info("Booting Blaze Server"))
      .onFinalize(Logger[F].info("Booting Blaze Server").void)
}
