package com.pinkstack.tocenlive

import cats.effect.std.Console
import cats.effect.{Async, Ref, Sync, Temporal}
import cats.implicits._
import fs2.concurrent.Topic
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import sttp.client3.SttpBackend

import java.util.UUID

final case class OnTimeObserver[F[_]: Async](environment: (TocenLiveConfig, SttpBackend[F, Any]),
                                             changesTopic: Topic[F, Change])(
    implicit F: Temporal[F],
    console: Console[F],
    logger: Logger[F]) {

  implicit def log[G[_]: Sync]: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]

  private val setup: (Ref[F, Boolean], Ref[F, Map[UUID, BusInfo]]) => F[Unit] = {
    case (firstLoopRef: Ref[F, Boolean], busesRef: Ref[F, Map[UUID, BusInfo]]) => {
      for {
        out <- (
          busesRef.get,
          OnTimeClient[F]().buses(environment).map(_.map(b => (b.bus_id, b)).toMap),
          firstLoopRef.get
        ).mapN {
          case (old, current, false) => (current, ChangeDetector.changes(old, current))
          case (_, current, true)    => (current, Seq.empty[Change])
        }
        v <- {
          logger.info(s"Changes collected: %d".formatted(out._2.size))
        } *> Sync[F].delay(out._2.map(changesTopic.publish1))
        _ <- v.sequence
        _ <- busesRef.set(out._1) >> Async[F].sleep(environment._1.refreshInterval)
      } yield ()
    } >> firstLoopRef.set(false)
  }

  def watch(): F[Unit] =
    logger.info("Started watching OnTimeAPI") >> (
      Ref[F].of(true),
      Ref[F].of(Map.empty[UUID, BusInfo])
    ).tupled
      .flatMap(v => setup(v._1, v._2).foreverM.void)
}
