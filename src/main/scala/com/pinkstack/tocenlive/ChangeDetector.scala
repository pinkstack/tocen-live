package com.pinkstack.tocenlive

import cats.implicits._
import java.util.UUID

sealed trait Change {
  def busInfo: BusInfo
}

object Change {
  final case class Added(busInfo: BusInfo) extends Change

  final case class Updated(busInfo: BusInfo, previous: BusInfo) extends Change

  final case class Removed(busInfo: BusInfo) extends Change
}

object ChangeDetector {

  import Change._

  type Buses         = Map[UUID, BusInfo]
  type DetectChanges = (Buses, Buses) => Seq[Change]

  private val updates: DetectChanges = (oldInfos, currentInfos) =>
    for {
      previous <- oldInfos.values.toIndexedSeq
      current  <- currentInfos.values
      change = Updated(current, previous)
      if previous != current && previous.bus_id == current.bus_id
    } yield change

  private val additions: DetectChanges = (old, current) =>
    current
      .filterNot(c => old.contains(c._1))
      .map { case (_, busInfo) => Added(busInfo) }
      .toIndexedSeq

  private val removals: DetectChanges = (old, current) =>
    additions(current, old).map(add => Removed(add.busInfo))

  private def combineDetectors(detectors: DetectChanges*): DetectChanges =
    detectors.toList.foldMap(_.curried).apply(_)(_)

  val changes: DetectChanges = combineDetectors(additions, removals, updates)
}
