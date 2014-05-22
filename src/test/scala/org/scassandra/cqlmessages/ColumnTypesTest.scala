package org.scassandra.cqlmessages

import org.scalatest.{Matchers, FunSuite}

class ColumnTypesTest extends FunSuite with Matchers {
  test("Serialisation of CqlInt") {
    CqlInt.writeValue(BigDecimal("123")) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    CqlInt.writeValue("123") should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))

    intercept[IllegalArgumentException] {
      CqlInt.writeValue(BigDecimal("123.67"))
    }
    intercept[IllegalArgumentException] {
      CqlInt.writeValue(BigDecimal("12345678910"))
    }
    intercept[IllegalArgumentException] {
      CqlInt.writeValue("hello") should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
    intercept[IllegalArgumentException] {
      CqlInt.writeValue(true) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
    intercept[IllegalArgumentException] {
      CqlInt.writeValue(false) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
    intercept[IllegalArgumentException] {
      CqlInt.writeValue(List()) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
    intercept[IllegalArgumentException] {
      CqlInt.writeValue(Map) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
  }

  test("Serialisation of CqlBigInt") {
    CqlBigint.writeValue(BigDecimal("123000000000")) should equal(Array(0, 0, 0, 8, 0, 0, 0, 28, -93, 95, 14, 0))
    CqlBigint.writeValue("123") should equal(Array[Byte](0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 123))

    intercept[IllegalArgumentException] {
      CqlBigint.writeValue(BigDecimal("123.67"))
    }
    intercept[IllegalArgumentException] {
      CqlBigint.writeValue("hello") should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
    intercept[IllegalArgumentException] {
      CqlBigint.writeValue(true) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
    intercept[IllegalArgumentException] {
      CqlBigint.writeValue(false) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
    intercept[IllegalArgumentException] {
      CqlBigint.writeValue(List()) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
    intercept[IllegalArgumentException] {
      CqlBigint.writeValue(Map) should equal(Array[Byte](0, 0, 0, 4, 0, 0, 0, 123))
    }
  }

  test("Serialisation of CqlVarint") {
    CqlVarint.writeValue(BigDecimal("123000000000")) should equal(Array(0, 0, 0, 5, 28, -93, 95, 14, 0))
    CqlVarint.writeValue("123") should equal(Array(0, 0, 0, 1, 123))

    intercept[IllegalArgumentException] {
      CqlVarint.writeValue(BigDecimal("123.67"))
    }
    intercept[IllegalArgumentException] {
      CqlVarint.writeValue("hello")
    }
    intercept[IllegalArgumentException] {
      CqlVarint.writeValue(true)
    }
    intercept[IllegalArgumentException] {
      CqlVarint.writeValue(false)
    }
    intercept[IllegalArgumentException] {
      CqlVarint.writeValue(List())
    }
    intercept[IllegalArgumentException] {
      CqlVarint.writeValue(Map)
    }
  }

  test("Serialisation of CqlDouble") {
    CqlDouble.writeValue(BigDecimal("123")) should equal(Array(0, 0, 0, 8, 64, 94, -64, 0, 0, 0, 0, 0))
    CqlDouble.writeValue("123") should equal(Array(0, 0, 0, 8, 64, 94, -64, 0, 0, 0, 0, 0))
    CqlDouble.writeValue(BigDecimal("123.67")) should equal(Array(0, 0, 0, 8, 64, 94, -22, -31, 71, -82, 20, 123))

    intercept[IllegalArgumentException] {
      CqlDouble.writeValue("hello")
    }
    intercept[IllegalArgumentException] {
      CqlDouble.writeValue(true)
    }
    intercept[IllegalArgumentException] {
      CqlDouble.writeValue(false)
    }
    intercept[IllegalArgumentException] {
      CqlDouble.writeValue(List())
    }
    intercept[IllegalArgumentException] {
      CqlDouble.writeValue(Map)
    }
  }

  test("Serialisation of CqlFloat") {
    CqlFloat.writeValue(BigDecimal("123")) should equal(Array(0, 0, 0, 4, 66, -10, 0, 0))
    CqlFloat.writeValue("123") should equal(Array(0, 0, 0, 4, 66, -10, 0, 0))
    CqlFloat.writeValue(BigDecimal("123.67")) should equal(Array(0, 0, 0, 4, 66, -9, 87, 10))

    intercept[IllegalArgumentException] {
      CqlFloat.writeValue("hello")
    }
    intercept[IllegalArgumentException] {
      CqlFloat.writeValue(true)
    }
    intercept[IllegalArgumentException] {
      CqlFloat.writeValue(false)
    }
    intercept[IllegalArgumentException] {
      CqlFloat.writeValue(List())
    }
    intercept[IllegalArgumentException] {
      CqlFloat.writeValue(Map)
    }
  }


  test("Serialisation of CqlCounter") {
    CqlCounter.writeValue(BigDecimal("123000000000")) should equal(Array(0, 0, 0, 8, 0, 0, 0, 28, -93, 95, 14, 0))
    CqlCounter.writeValue("123") should equal(Array[Byte](0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 123))

    intercept[IllegalArgumentException] {
      CqlCounter.writeValue(BigDecimal("123.67"))
    }
    intercept[IllegalArgumentException] {
      CqlCounter.writeValue("hello")
    }
    intercept[IllegalArgumentException] {
      CqlCounter.writeValue(true)
    }
    intercept[IllegalArgumentException] {
      CqlCounter.writeValue(false)
    }
    intercept[IllegalArgumentException] {
      CqlCounter.writeValue(List())
    }
    intercept[IllegalArgumentException] {
      CqlCounter.writeValue(Map)
    }
  }

}