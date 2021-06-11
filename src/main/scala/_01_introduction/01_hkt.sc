// Higher Kinded Types
trait Foo[C[_]] {
  def create(i: Int): C[Int]
}

object FooList extends Foo[List] {
  override def create(i: Int): List[Int] = List(i)
}

val list = FooList.create(10)

type EitherString[T] = Either[String, T]

object FooEitherString extends Foo[EitherString] {
//  this is same return type as below
//  override def create(i: Int): EitherString[Int] = Right(i)
  override def create(i: Int): Either[String, Int] = Right(i)
}

val eitherString = FooEitherString.create(7)

// Using kind-projector plugin -- why it's not working in worksheet
//object FooEitherString extends Foo[Either[String, ?]] {
//  override def create(i: Int): Either[String, Int] = Right(i)
//}

type Id[T] = T

object FooId extends Foo[Id] {
  override def create(i: Int): Id[Int] = i
}

val id = FooId.create(9)


