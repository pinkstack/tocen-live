package com.pinkstack.tocenlive

import cats.effect.{Async, Ref, Sync}
import cats.implicits._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import sttp.client3.SttpBackend

import java.util.UUID
import scala.concurrent.duration.DurationInt

final case class OnTimeWatcher[F[_] : Sync](busNumberRef: Ref[F, Int])
                                           (environment: (TocenLiveConfig, SttpBackend[F, Any]))
                                           (implicit F: Async[F]) {
  import io.circe.generic.auto._, io.circe.syntax._
  import ChangeDetector._

  implicit def log[G[_] : Sync]: SelfAwareStructuredLogger[F] =
    Slf4jLogger.getLoggerFromName[F]("ontime-watcher")

  private val setup: (Ref[F, Boolean], Ref[F, Map[UUID, BusInfo]]) => F[Unit] = {
    case (firstLoopRef: Ref[F, Boolean], busesRef: Ref[F, Map[UUID, BusInfo]]) => {
      {
        for {
          v <- busNumberRef.getAndUpdate(_ + 10)
          _ <- Logger[F].info(s"Loop ${v}")
          out <- (busesRef.get, OnTimeClient[F]().buses(environment).map(_.map(b => (b.bus_id, b)).toMap), firstLoopRef.get).mapN {
            case (old, current, false) => ChangeDetector.changes(old, current)
            case (_, current, true) => (current, Seq.empty[Change])
          }
          _ <- Async[F].delay {
            out._2.foreach { change =>
              println(change.asJson)
            }
          }
          _ <- busesRef.set(out._1) >> Async[F].sleep(1.second)
        } yield ()
      } >> firstLoopRef.set(false)
    }.foreverM.void
  }

  def watch(): F[Unit] = {
    Logger[F].info("Started watching OnTimeAPI") >> (Ref[F].of(true), Ref[F].of(Map.empty[UUID, BusInfo])).tupled
      .flatMap(v => setup(v._1, v._2))
  }
}
