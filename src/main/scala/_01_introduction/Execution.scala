package _01_introduction

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
