package com.pinkstack.tocenlive

import cats.effect._
import cats.implicits._
import fs2.concurrent.Topic
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend

object Main extends IOApp.Simple {
  def mkAsyncBackend[F[_]: Async: Logger](): Resource[F, SttpBackend[F, Any]] =
    AsyncHttpClientCatsBackend
      .resource[F]()
      .evalTap(_ => Logger[F].info("Booting Async HTTP backend."))
      .onFinalize(Logger[F].info("Async HTTP backend done."))

  def mkResources[F[_]: Async: Logger](): Resource[F, (TocenLiveConfig, SttpBackend[F, Any])] =
    (Configuration.mkResource[F](), mkAsyncBackend[F]()).tupled

  def run: IO[Unit] = {
    implicit def logger[F[_]: Sync]: SelfAwareStructuredLogger[F] =
      Slf4jLogger.getLoggerFromName[F]("tocen-live")

    for {
      _ <- Logger[IO].info("Booting...")
      changes <- Topic[IO, Change]
      _ <- mkResources[IO]().use {
        case (config, backend) =>
          for {
            _ <- OnTimeObserver[IO]((config, backend), changes).watch().start
            _ <- WebServer[IO](changes).mkResource().use(_ => IO.never).foreverM.void
          } yield ()
      }
    } yield ()
  }
}
