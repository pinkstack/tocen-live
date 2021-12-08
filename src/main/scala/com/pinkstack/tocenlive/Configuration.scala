package com.pinkstack.tocenlive

import cats.effect.{Resource, Sync}
import pureconfig._
import pureconfig.generic.auto._

final case class TocenLiveConfig(tocenApiKey: String)

object Configuration {
  def load[F[_] : Sync](): F[TocenLiveConfig] =
    Sync[F].pure(ConfigSource.default.at("tocen-live")
      .loadOrThrow[TocenLiveConfig])

  def mkResource[F[_] : Sync](): Resource[F, TocenLiveConfig] = Resource.liftK(load())
}
