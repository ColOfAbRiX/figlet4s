package com.colofabrix.scala.figlet4s.core

import cats._
import com.colofabrix.scala.figlet4s.errors._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BraketSpecs extends AnyFlatSpec with Matchers with MockFactory {

  val expected: Id[Int] = 123

  "withResource" should "apply the function, close the resource and return the value" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .returning(expected)

    val actual = Braket.withResource(mockResource)(mockUserFunction)

    actual shouldBe expected
  }

  it should "throw a FigletLoadingError when the function fails" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall { _: AutoCloseable => throw new RuntimeException("failure") }

    val caught =
      intercept[FigletLoadingError] {
        Braket.withResource(mockResource)(mockUserFunction)
      }

    caught.getMessage() shouldBe "failure"
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

    val caught =
      intercept[RuntimeException] {
        Braket.withResource(mockResource)(mockUserFunction)
      }

    caught.getMessage() shouldBe "close failure"
  }

  it should "throw a FigletLoadingError when the function and the close() fail" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()
      .onCall(_ => throw new RuntimeException("close failure"))

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall { _: AutoCloseable => throw new RuntimeException("failure") }

    val caught =
      intercept[FigletLoadingError] {
        Braket.withResource(mockResource)(mockUserFunction)
      }

    caught.getMessage() shouldBe "failure"
    caught.getSuppressed.map(_.getMessage()).mkString shouldBe "close failure"
  }

}
