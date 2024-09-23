package com.colofabrix.scala.figlet4s.core

import cats._
import cats.effect._
import cats.scalatest._
import com.colofabrix.scala.figlet4s.errors._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class BraketSpecs extends AnyFlatSpec with Matchers with MockFactory with EitherMatchers with EitherValues {

  private val expected: Int = 123

  //  Unsafe  //

  "Unsafe withResource" should "apply the user function, close the resource and return the value" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .returning(expected)

    val actual = Braket.withResource[Id, AutoCloseable, Int](mockResource)(mockUserFunction)

    actual shouldBe expected
  }

  it should "throw a FigletLoadingError when the user function fails" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall { _: AutoCloseable =>
        throw new RuntimeException("failure")
      }

    val actual = intercept[FigletLoadingError] {
      Braket.withResource[Id, AutoCloseable, Int](mockResource)(mockUserFunction)
    }

    actual.getMessage shouldBe "failure"
  }

  it should "throw a RuntimeException when close() fails" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()
      .onCall(_ => throw new RuntimeException("close failure"))

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .returning(expected)

    val actual = intercept[RuntimeException] {
      Braket.withResource[Id, AutoCloseable, Int](mockResource)(mockUserFunction)
    }

    actual.getMessage shouldBe "close failure"
  }

  it should "throw a FigletLoadingError when the user function and the close() fail" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()
      .onCall(_ => throw new RuntimeException("close failure"))

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall { _: AutoCloseable => throw new RuntimeException("failure") }

    val actual = intercept[FigletLoadingError] {
      Braket.withResource[Id, AutoCloseable, Int](mockResource)(mockUserFunction)
    }

    actual.getMessage shouldBe "failure"
    actual.getSuppressed.map(_.getMessage()).mkString shouldBe "close failure"
  }

  //  Either  //

  "Either withResource" should "apply the user function, close the resource and return the value" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, Either[Throwable, Int]]
    mockUserFunction
      .expects(mockResource)
      .returning(Right(expected))

    type ET[A] = Either[Throwable, A]
    val actual: ET[Int] = Braket.withResource[ET, AutoCloseable, Int](mockResource)(mockUserFunction)

    actual should be(right)
    actual.value shouldBe expected
  }

  it should "return a FigletLoadingError when the user function fails" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, Either[Throwable, Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall { _: AutoCloseable =>
        throw new RuntimeException("failure")
      }

    type ET[A] = Either[Throwable, A]
    val actual: ET[Int] = Braket.withResource[ET, AutoCloseable, Int](mockResource)(mockUserFunction)

    actual should be(left)
    actual.leftValue shouldBe a[FigletLoadingError]
    actual.leftValue.getMessage shouldBe "failure"
  }

  it should "return a RuntimeException when close() fails" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()
      .onCall(_ => throw new RuntimeException("close failure"))

    val mockUserFunction = mockFunction[AutoCloseable, Either[Throwable, Int]]
    mockUserFunction
      .expects(mockResource)
      .returning(Right(expected))

    type ET[A] = Either[Throwable, A]
    val actual: ET[Int] = Braket.withResource[ET, AutoCloseable, Int](mockResource)(mockUserFunction)

    actual should be(left)
    actual.leftValue shouldBe a[RuntimeException]
    actual.leftValue.getMessage shouldBe "close failure"
  }

  it should "return a FigletLoadingError when the user function and the close() fail" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()
      .onCall(_ => throw new RuntimeException("close failure"))

    val mockUserFunction = mockFunction[AutoCloseable, Either[Throwable, Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall { _: AutoCloseable => throw new RuntimeException("failure") }

    type ET[A] = Either[Throwable, A]
    val actual: ET[Int] = Braket.withResource[ET, AutoCloseable, Int](mockResource)(mockUserFunction)

    actual should be(left)
    actual.leftValue shouldBe a[FigletLoadingError]
    actual.leftValue.getMessage shouldBe "failure"
    actual.leftValue.getSuppressed.map(_.getMessage()).mkString shouldBe "close failure"
  }

  //  Cats IO  //

  "IO withResource" should "apply the user function, close the resource and return the value" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, IO[Int]]
    mockUserFunction
      .expects(mockResource)
      .returning(IO(expected))

    val actual =
      Braket
        .withResource[IO, AutoCloseable, Int](mockResource)(mockUserFunction)
        .unsafeRunSync()

    actual shouldBe expected
  }

  it should "return a FigletLoadingError when the user function fails" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, IO[Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall { _: AutoCloseable =>
        throw new RuntimeException("failure")
      }

    val actual =
      Braket
        .withResource[IO, AutoCloseable, Int](mockResource)(mockUserFunction)
        .redeem(identity, _ => fail("Call to withResource didn't return an Exception"))
        .unsafeRunSync()

    actual shouldBe a[FigletLoadingError]
    actual.getMessage shouldBe "failure"
  }

  it should "return a RuntimeException when close() fails" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()
      .onCall(_ => throw new RuntimeException("close failure"))

    val mockUserFunction = mockFunction[AutoCloseable, IO[Int]]
    mockUserFunction
      .expects(mockResource)
      .returning(IO(expected))

    val actual =
      Braket
        .withResource[IO, AutoCloseable, Int](mockResource)(mockUserFunction)
        .redeem(identity, _ => fail("Call to withResource didn't return an Exception"))
        .unsafeRunSync()

    actual shouldBe a[RuntimeException]
    actual.getMessage shouldBe "close failure"
  }

  it should "return a FigletLoadingError when the user function and the close() fail" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()
      .onCall(_ => throw new RuntimeException("close failure"))

    val mockUserFunction = mockFunction[AutoCloseable, IO[Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall { _: AutoCloseable => throw new RuntimeException("failure") }

    val actual =
      Braket
        .withResource[IO, AutoCloseable, Int](mockResource)(mockUserFunction)
        .redeem(identity, _ => fail("Call to withResource didn't return an Exception"))
        .unsafeRunSync()

    actual shouldBe a[FigletLoadingError]
    actual.getMessage shouldBe "failure"
    actual.getSuppressed.map(_.getMessage()).mkString shouldBe "close failure"
  }

}
