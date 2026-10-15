package com.colofabrix.scala.figlet4s.core

import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._

class BraketSpecs extends AnyFlatSpec with Matchers with MockFactory {

  val expected: Int = 123

  private val F = FigletSync[Id]

  //  Unsafe  //

  "Unsafe withResource" should "apply the user function, close the resource and return the value" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .returning(expected)

    val actual = F.withResource(mockResource)(mockUserFunction)

    actual shouldBe expected
  }

  it should "throw a FigletLoadingError when the user function fails" in {
    val mockResource = mock[AutoCloseable]
    (mockResource.close _)
      .expects()

    val mockUserFunction = mockFunction[AutoCloseable, Id[Int]]
    mockUserFunction
      .expects(mockResource)
      .onCall {
        _: AutoCloseable =>
          throw new RuntimeException("failure")
      }

    val actual = intercept[FigletLoadingError] {
      F.withResource(mockResource)(mockUserFunction)
    }

    actual.getMessage() shouldBe "failure"
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
      F.withResource(mockResource)(mockUserFunction)
    }

    actual.getMessage() shouldBe "close failure"
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
      F.withResource(mockResource)(mockUserFunction)
    }

    actual.getMessage() shouldBe "failure"
    actual.getSuppressed.map(_.getMessage()).mkString shouldBe "close failure"
  }

}
