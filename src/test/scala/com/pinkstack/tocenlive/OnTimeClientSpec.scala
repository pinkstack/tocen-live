package com.pinkstack.tocenlive

import cats.effect._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.testing.SttpBackendStub
import sttp.model.StatusCode

import scala.io.Source
import scala.concurrent.duration.DurationInt

class OnTimeClientSpec extends AsyncSpec {
  implicit def log[F[_]: Sync] =
    Slf4jLogger.getLogger

  private lazy val readBody: String => String =
    name => Source.fromResource(name).mkString

  private def mkBackend(): SttpBackendStub[IO, Any] =
    AsyncHttpClientCatsBackend.stub[IO]

  private def environmentWithResponse[T](
      body: T,
      statusCode: StatusCode = StatusCode.Ok
  ): (TocenLiveConfig, SttpBackendStub[IO, Any]) =
    (
      TocenLiveConfig("test", 400.milliseconds),
      mkBackend()
        .whenRequestMatches(_ => true)
        .thenRespond[T](body, statusCode)
    )

  "OnTimeClient" should "work for valid response" in {
    OnTimeClient[IO]().buses(environmentWithResponse(readBody("buses.json"))).asserting {
      busInfos =>
        busInfos shouldBe a[List[_]]
        busInfos should not be empty
        busInfos.headOption should matchPattern {
          case Some(BusInfo(_, "LJ LP-408", _, _, _, _, _, _, _, _)) =>
        }
    }
  }

  it should "fail when payload is broken" in {
    OnTimeClient[IO]()
      .buses(environmentWithResponse(readBody("buses-broken.json")))
      .assertThrows[Exception]
  }

  it should "fail when API key is invalid" in {
    OnTimeClient[IO]()
      .buses(environmentWithResponse(readBody("api-fail-403.json"), StatusCode.apply(403)))
      .assertThrows[Exception]
  }
}
