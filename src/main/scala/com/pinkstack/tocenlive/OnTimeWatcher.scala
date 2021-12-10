package com.pinkstack.tocenlive

import cats.effect.{Async, Ref, Sync}
import cats.implicits._
import com.pinkstack.tocenlive.Change.{Added, Removed, Updated}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import sttp.client3.SttpBackend

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

final case class Event[T](
    kind: String,
    data: T,
    id: UUID = UUID.randomUUID(),
    created_at: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
)

object Event {

  import io.circe.Encoder
  import io.circe.generic.auto._
  import io.circe.syntax._

  implicit val encodeChange: Encoder[Change] = Encoder.instance {
    case added @ Added(_)        => added.asJson
    case updated @ Updated(_, _) => updated.asJson
    case removed @ Removed(_)    => removed.asJson
  }
}

final case class OnTimeWatcher[F[_]: Sync](busNumberRef: Ref[F, Int])(
    environment: (TocenLiveConfig, SttpBackend[F, Any])
)(implicit F: Async[F]) {

  import io.circe.generic.auto._
  import io.circe.syntax._

  implicit def log[G[_]: Sync]: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]

  private val setup: (Ref[F, Boolean], Ref[F, Map[UUID, BusInfo]]) => F[Unit] = {
    case (firstLoopRef: Ref[F, Boolean], busesRef: Ref[F, Map[UUID, BusInfo]]) => {
      {
        for {
          v <- busNumberRef.getAndUpdate(_ + 10)
          out <- (
            busesRef.get,
            OnTimeClient[F]().buses(environment).map(_.map(b => (b.bus_id, b)).toMap),
            firstLoopRef.get
          ).mapN {
              case (old, current, false) => (current, ChangeDetector.changes(old, current))
              case (_, current, true)    => (current, Seq.empty[Change])
            }
          _ <- log.info(s"Changes collected: %d".formatted(out._2.size))
          /*
          _ <- Async[F].delay {
            out._2.foreach { change =>
              println(Event(change.getClass.getSimpleName, change).asJson.noSpaces)
            }
          } */
          _ <- busesRef.set(out._1) >> Async[F].sleep(environment._1.refreshInterval)
        } yield ()
      } >> firstLoopRef.set(false)
    }.foreverM.void
  }

  def watch(): F[Unit] = {
    Logger[F].info("Started watching OnTimeAPI") >> (
      Ref[F].of(true),
      Ref[F].of(Map.empty[UUID, BusInfo])
    ).tupled
      .flatMap(v => setup(v._1, v._2))
  }
}
