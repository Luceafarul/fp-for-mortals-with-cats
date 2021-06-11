package _01_introduction

import scala.io.StdIn

object App extends App {
  def echoV1[C[_]](t: Terminal[C], e: Execution[C]): C[String] =
    e.chain(t.read) { in: String =>
      e.chain(t.write(in)) { _: Unit =>
        e.create(in)
      }
    }

  import Execution._

  def echoV2[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    t.read.flatMap { in: String =>
      t.write(in).map { _: Unit =>
        in
      }
    }

  def echoV3[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    for {
      in <- t.read
      _ <- t.write(in)
    } yield in

  implicit object TerminalIO extends Terminal[IO] {
    override def read: IO[String] = IO { StdIn.readLine() }
    override def write(s: String): IO[Unit] = IO { println(s) }
  }

  implicit object ExecutionIO extends Execution[IO] {
    override def chain[A, B](c: IO[A])(f: A => IO[B]): IO[B] = IO(f(c.interpret()).interpret())
    override def create[B](b: B): IO[B] = IO(b)
  }

  val delayed: IO[String] = echoV3[IO]

  delayed.interpret()
}
