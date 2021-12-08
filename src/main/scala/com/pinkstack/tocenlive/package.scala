package com.pinkstack

import java.util.UUID

package object tocenlive {

  case class BusInfo(bus_id: UUID,
                     bus_name: String,
                     bus_group: String,
                     lat: Double,
                     lng: Double,
                     rotation: Double,
                     velocity: Double,
                     odometer: BigInt,
                     trip_id: Option[UUID] = None,
                     last_stop_id: Option[UUID] = None)

}
