package com.pinkstack.tocenlive

import com.pinkstack.tocenlive.Change._
import java.util.UUID

class ChangeDetectorSpec extends BaseSpec {

  def makeFakeInfo(): BusInfo = BusInfo(
    UUID.randomUUID(),
    "LJ-111",
    "11",
    11.1,
    46.1,
    rotation = 0.1,
    velocity = 0.2,
    odometer = 10_000,
    None,
    None
  )

  "ChangeDetector" should "detect 0 changes" in {
    val b1       = makeFakeInfo()
    val newState = Map(b1.bus_id -> b1)
    val changes  = ChangeDetector.changes(newState, newState)
    changes shouldBe empty
  }

  it should "detect additions" in {
    val b1 = makeFakeInfo()
    val b2 = makeFakeInfo()
    val changes =
      ChangeDetector.changes(Map(b1.bus_id -> b1), Map(b1.bus_id -> b1, b2.bus_id -> b2))
    changes should contain(Added(b2))
    ChangeDetector.changes(Map.empty, Map(b1.bus_id -> b1)) should contain(Added(b1))
  }

  it should "detect removals" in {
    val b1 = makeFakeInfo()
    val b2 = makeFakeInfo()
    val changes =
      ChangeDetector.changes(Map(b1.bus_id -> b1, b2.bus_id -> b2), Map(b1.bus_id -> b1))
    changes should contain(Removed(b2))
  }

  it should "detect updates" in {
    val b1         = makeFakeInfo()
    val b1_changed = b1.copy(lat = b1.lat + 0.42, lng = b1.lng + 0.42)
    val initial    = Map(b1.bus_id -> b1)
    val newState   = initial.updated(b1.bus_id, b1_changed)

    val changes = ChangeDetector.changes(initial, newState)
    changes should contain(Updated(b1_changed, b1))
  }
}
