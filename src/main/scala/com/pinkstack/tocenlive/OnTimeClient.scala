package com.pinkstack.tocenlive

import cats.effect.Async
import cats.implicits._
import io.circe.Json
import io.circe.generic.auto._
import org.typelevel.log4cats.Logger
import sttp.client3._
import sttp.client3.circe.asJson

final case class OnTimeClient[F[_]: Async: Logger]() {
  type Environment = (TocenLiveConfig, SttpBackend[F, _])
  private val root = uri"https://api.ontime.si"

  private val getBuses: Environment => F[Json] = {
    case (config, backend) =>
      basicRequest
        .get(root.withWholePath("/api/v1/lpp/buses/"))
        .contentType("application/json")
        .acceptEncoding("gzip, deflate")
        .headers(Map("authorization" -> s"Api-Key ${config.tocenApiKey}"))
        .response(asJson[Json].getRight)
        .send(backend)
        .map(_.body)
  }

  private val convert: F[Json] => F[List[BusInfo]] =
    _.map(_.hcursor.get[List[BusInfo]]("results")).rethrow

  val buses: Environment => F[List[BusInfo]] = getBuses >>> convert
}
