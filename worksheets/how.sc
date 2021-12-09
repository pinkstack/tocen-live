import cats._
import cats.implicits._

val f1: (Int, Int) => Seq[Int] = (n, m) => Seq.fill(n)(0).map(_ * m)
val f2: (Int, Int) => Seq[Int] = (n, m) => Seq.fill(n)(40).map(_ * m)
val f3: (Int, Int) => Seq[Int] = (n, m) => Seq.fill(n)(42).map(_ * m)

val together: (Int, Int) => Seq[Int] = (n, m) => Seq(f1, f2, f3).flatMap(f => f(n, m))

together(2, 3)

// Semigroups
Semigroup[Int].combine(1, 1)

// Monoids
object FSyntax {
  type Input = (Int, Int)
  type Output = Seq[Int]
  type M = Input => Output
  implicit val monoidForF = new Monoid[Input => Output] {
    def empty() = (_, _) => Seq.empty[Int]

    def combine(x: M, y: M) =
      (n: Int, m: Int) => Seq(x, y).flatMap(_.apply(n, m))

  }
}

val vf2 = f1 |+| f2 |+| f3
vf2(2, 3)

assert(together(2, 3) == vf2(2, 3))
