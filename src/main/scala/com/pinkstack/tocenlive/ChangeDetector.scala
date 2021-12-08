package com.pinkstack.tocenlive

import java.util.UUID

object ChangeDetector {
  type Buses = Map[UUID, BusInfo]
  type Detector = (Buses, Buses) => (Buses, Seq[Change])

  sealed trait Change {
    def busInfo: BusInfo
  }

  final case class Added(busInfo: BusInfo) extends Change

  final case class Updated(busInfo: BusInfo, previous: BusInfo) extends Change

  final case class Removed(busInfo: BusInfo) extends Change

  private val updates: (Buses, Buses) => Seq[Change] = (oldInfos, currentInfos) =>
    for {
      previous <- oldInfos.values.toIndexedSeq
      current <- currentInfos.values
      change = Updated(current, previous) if previous != current && previous.bus_id == current.bus_id
    } yield change

  private val additions: (Buses, Buses) => Seq[Change] = (old, current) =>
    current.filterNot(c => old.contains(c._1)).map { case (_, busInfo) => Added(busInfo) }.toIndexedSeq

  private val removals: (Buses, Buses) => Seq[Change] = (old, current) =>
    additions.apply(current, old).map(add => Removed.apply(add.busInfo))

  private val aggChanges: (Buses, Buses) => Seq[Change] = (old, current) =>
    Seq(updates, additions, removals).flatMap(f => f(old, current))

  val changes: Detector = (old, current) =>
    (current, aggChanges(old, current))
}
