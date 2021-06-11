import scala.concurrent.Future
import scala.io.StdIn

// How do we write generic code that does something as simple as echo
// the user’s input synchronously or asynchronously
// depending on our runtime implementation?
trait TerminalSyncOld {
  def read(): String
  def write(s: String): Unit
}

trait TerminalAsyncOld {
  def read(): Future[String]
  def write(s: String): Future[Unit]
}

// We can think of C as a Context because
// we say “in the context of executing Now” or “in the Future”.
trait Terminal[C[_]] {
  def read: C[String]
  def write(s: String): C[Unit]
}

type Now[X] = X

implicit object TerminalSync extends Terminal[Now] {
  override def read: String = "Now"
  override def write(s: String): Unit = println(s)
}

object TerminalAsync extends Terminal[Future] {
  override def read: Future[String] = Future.successful(StdIn.readLine())
  override def write(s: String): Future[Unit] = Future.successful(println(s))
}

// But we know nothing about C and we cannot do anything with a C[String]
trait Execution[C[_]] {
  def chain[A, B](c: C[A])(f: A => C[B]): C[B]
  def create[B](b: B): C[B]
}

object Execution {
  implicit class Ops[A, C[_]](c: C[A]) {
    def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] =
      e.chain(c)(f)
    def map[B](f: A => B)(implicit e: Execution[C]): C[B] =
      e.chain(c)(a => e.create(f(a)))
  }
}

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

implicit object ExecutionNow extends Execution[Now] {
  override def chain[A, B](c: Now[A])(f: A => Now[B]): Now[B] = f(c)
  override def create[B](b: B): Now[B] = b
}

// Need mark TerminalSync and ExecutionNow with implicit
// or passed are explicitly
echoV3[Now]

final class IO[A](val interpret: () => A) {
  def map[B](f: A => B): IO[B] = IO(f(interpret()))
  def flatMap[B](f: A => IO[B]): IO[B] = IO(f(interpret()).interpret()) // TODO why we use here interpret 2 time
}

object IO {
  def apply[A](a: A): IO[A] = new IO(() => a)
}

implicit object TerminalIO extends Terminal[IO] {
  override def read: IO[String] = IO { StdIn.readLine() }
  override def write(s: String): IO[Unit] = IO { println(s) }
}

implicit object ExecutionIO extends Execution[IO] {
  override def chain[A, B](c: IO[A])(f: A => IO[B]) = IO(f(c.interpret()).interpret())
  override def create[B](b: B) = IO(b)
}

// DO NOT USE IN WORKSHEET
//val delayed: IO[String] = echoV3[IO]
