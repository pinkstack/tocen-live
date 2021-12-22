package com.pinkstack.tocenlive

import cats._
import cats.implicits._
import cats.effect._
import fs2.{Chunk, Pipe, Stream}

import java.time.LocalDateTime
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object StreamsExampleApp extends IOApp.Simple {

  def example1(): IO[List[Int]] = {
    Stream
      .emits(Seq(5, 1, 2, 3, 42))
      .map(_ * 2)
      .filter(_ % 2 == 0)
      .covary[IO]
      .compile
      .toList
    // .drain
  }

  def example2(): IO[Unit] = {
    val st: Stream[IO, Boolean] = Stream(false) ++ Stream(true).repeat

    Stream
      .awakeEvery[IO](1.seconds)
      .map(_ => LocalDateTime.now())
      .zip(st)
      .filter(_._2)
      .evalTap(p => IO(println(p)))
      .covary[IO]
      .compile
      .drain
  }

  def example3(): IO[_] = {
    IO("Hello from example 3").void
    val isFirst: Stream[IO, Boolean] = Stream(false) ++ Stream(true).repeat

    def doSomething(): IO[LocalDateTime] =
      IO.fromFuture(IO(Future.successful(LocalDateTime.now)))

    def aggregator(n: Int): Pipe[IO, (Boolean, LocalDateTime), Int] =
      in =>
        in.scanChunksOpt(n) { a =>
          if (a >= 5) None
          else
            Some((c: Chunk[(Boolean, LocalDateTime)]) =>
              (a + 1, Chunk(a))
            )
      }

    Stream
      .awakeEvery[IO](1.seconds)
      .zip(isFirst)
      .map(_._2)
      .mapAsyncUnordered(3)(isFirst => doSomething().map(d => (isFirst, d)))
      .through(aggregator(42))
      .evalTap(p => IO(println(p)))
      .take(7)
      .covary[IO]
      .compile
      .toList
    // .drain

  }

  override def run: IO[Unit] = {
    for {
      _ <- IO.println("Hello")
      // ex1 <- example1()
      // _ <- IO.println(ex1)
      // ex2 <- example2()
      // _ <- IO.println(ex2)
      ex3 <- example3()
      _ <- IO.println(ex3)
    } yield ()
  }
}
