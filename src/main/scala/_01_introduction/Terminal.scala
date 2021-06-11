package _01_introduction

trait Terminal[C[_]] {
  def read: C[String]
  def write(s: String): C[Unit]
}
