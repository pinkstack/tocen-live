package com.pinkstack.tocenlive

import cats.effect.{Async, Resource, Temporal}
import cats.implicits._
import fs2.concurrent.Topic
import fs2.{Pipe, Stream}
import io.circe.{Encoder, Json}
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.Text
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.DurationInt

final case class WebServer[F[_]: Async: Logger](changesTopic: Topic[F, Change])(
    implicit F: Temporal[F]) {
  import WebServer._
  import io.circe.generic.auto._, io.circe.syntax._
  import org.http4s.dsl.io._
  import Event.implicits._

  def wsRoutes(wsb: WebSocketBuilder[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "ws" / "changes" =>
      val toClient: Stream[F, WebSocketFrame.Text] =
        changesTopic
          .subscribe(100)
          .delayBy(50.milliseconds)
          .map(changeToTextFrame)

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
object WebServer {
  import io.circe.generic.auto._, io.circe.syntax._
  import Event.implicits._

  def eventToJson[T](event: Event[T])(implicit encoder: Encoder[Event[T]]): Json = event.asJson

  val changeToTextFrame: Change => Text = change =>
    Text(eventToJson(changeToEvent(change)).noSpaces)
}
