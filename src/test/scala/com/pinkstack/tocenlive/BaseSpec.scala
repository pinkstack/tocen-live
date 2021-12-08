package com.pinkstack.tocenlive

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest._
import org.scalatest.flatspec._
import org.scalatest.matchers._

abstract class BaseSpec extends AnyFlatSpec
  with should.Matchers
  with OptionValues
  with EitherValues
  with Inside
  with Inspectors

abstract class AsyncSpec extends AsyncFlatSpec with AsyncIOSpec
  with should.Matchers
  with OptionValues
  with EitherValues
  with Inside
  with Inspectors
