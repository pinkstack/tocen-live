package com.pinkstack

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.util.UUID
import scala.reflect.ClassTag

package object tocenlive {

  case class BusInfo(
      bus_id: UUID,
      bus_name: String,
      bus_group: String,
      lat: Double,
      lng: Double,
      rotation: Double,
      velocity: Double,
      odometer: BigInt,
      trip_id: Option[UUID] = None,
      last_stop_id: Option[UUID] = None
  )

  import java.time.{LocalDateTime, ZoneOffset}
  import java.util.UUID

  final case class Event[T](kind: String,
                            data: T,
                            id: UUID = UUID.randomUUID(),
                            created_at: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC))

  object Event {
    import Change._
    import io.circe.Encoder
    import io.circe.generic.auto._
    import io.circe.syntax._

    object implicits {
      implicit def changeToEvent(change: Change): Event[Change] =
        Event(change.getClass.getSimpleName, change)

      implicit def encodeEvent[T: Encoder]: Encoder[Event[T]] = deriveEncoder

      implicit val encodeChange: Encoder[Change] = Encoder.instance {
        case added @ Added(_)        => added.asJson
        case updated @ Updated(_, _) => updated.asJson
        case removed @ Removed(_)    => removed.asJson
      }
    }
  }

  sealed trait Change {
    def busInfo: BusInfo
  }

  object Change {
    final case class Added(busInfo: BusInfo) extends Change

    final case class Updated(busInfo: BusInfo, previous: BusInfo) extends Change

    final case class Removed(busInfo: BusInfo) extends Change
  }
}
