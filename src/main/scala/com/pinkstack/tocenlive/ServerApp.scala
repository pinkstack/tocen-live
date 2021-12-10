package com.pinkstack.tocenlive

import cats._
import cats.effect.kernel.Resource
import cats.implicits._
import cats.effect.{IO, IOApp}
import fs2.{Pipe, Stream}
import org.http4s.HttpRoutes
import org.http4s.HttpRoutes._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.blaze.server._
import org.http4s.server.Server
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket._
import org.http4s.websocket.WebSocketFrame.Text

import java.time.LocalDateTime
import scala.concurrent.duration.DurationInt

object ServerApp extends IOApp.Simple {

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root => Ok("")
  }.orNotFound

  def wsRoutes(wsb: WebSocketBuilder[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "ws" / "echo" =>
      val toClient: Stream[IO, WebSocketFrame] =
        Stream.awakeEvery[IO](1.second).map(_ => Text(s"Ok + ${LocalDateTime.now}"))
      val fromClient2: Pipe[IO, WebSocketFrame, Unit] = _.void

      wsb.build(toClient, fromClient2)
  }

  def mkBlazeServer(): Resource[IO, Server] =
    BlazeServerBuilder[IO]
      .withBanner(Seq.empty)
      .bindHttp(8077, "0.0.0.0")
      .withHttpApp(helloWorldService)
      .withHttpWebSocketApp(wsRoutes(_).orNotFound)
      .resource
      .onFinalize(IO(println("Done with server")).void)


  def run: IO[Unit] =
    for {
      _ <- mkBlazeServer().use(_ => IO.never).void
    } yield ()
}
