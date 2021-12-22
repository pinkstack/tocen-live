package com.pinkstack.tocenlive

import java.util.UUID
import Event._
import Event.implicits._
import WebServer.eventToJson
import io.circe.Json

class EventSpec extends BaseSpec {
  def makeFakeInfo(): BusInfo = BusInfo(
    UUID.randomUUID(),
    "LJ-111",
    "11",
    11.1,
    46.1,
    rotation = 0.1,
    velocity = 0.2,
    odometer = 10000,
    None,
    None
  )

  "Event serialisation" should "serialise change" in {
    val b1 = makeFakeInfo()
    val b2 = b1.copy(lat = b1.lat + 0.2, lng = b1.lng + 0.2)

    val events: Seq[Json] = ChangeDetector
      .changes(Map(b1.bus_id -> b1), Map(b2.bus_id -> b2))
      .map(changeToEvent)
      .map(event => eventToJson(event))

    val currentBusId =
      events.head.hcursor.downField("data").downField("busInfo").get[UUID]("bus_id")
    currentBusId.value should equal(b1.bus_id)

    val previousBusId =
      events.head.hcursor.downField("data").downField("previous").get[UUID]("bus_id")
    previousBusId.value should equal(b1.bus_id)
  }
}
