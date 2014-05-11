package uk.co.scassandra.e2e

import uk.co.scassandra.AbstractIntegrationTest
import org.scalatest.concurrent.ScalaFutures
import uk.co.scassandra.priming.query.When
import uk.co.scassandra.cqlmessages.CqlVarchar

class PrimingOptionalFieldsTest extends AbstractIntegrationTest with ScalaFutures {

  //
  //
  // keyspace name
  //
  //

  test("Not priming keyspace name should return empty keyspace name") {

    // given
    val whenQuery = "select * from people"
    val rows: List[Map[String, String]] = List(Map("name" -> "Chris", "age" -> "19"))
    val columnTypes  = Map("name" -> CqlVarchar)
    prime(When(whenQuery), rows, "success", columnTypes)

    // when
    val result = session.execute(whenQuery)

    // then
    val actualKeyspace = result.getColumnDefinitions().getKeyspace(0)
    actualKeyspace should equal("")
  }

  test("Priming keyspace name should return expected keyspace name") {

    // given
    val whenQuery = "select * from people"
    val expectedKeyspace = "myKeyspace"
    val rows: List[Map[String, String]] = List(Map("name" -> "Chris", "age" -> "19"))
    val columnTypes  = Map("name" -> CqlVarchar)
    prime(When(whenQuery, keyspace = Some(expectedKeyspace)), rows, "success", columnTypes)

    // when
    val result = session.execute(whenQuery)

    // then
    val actualKeyspace = result.getColumnDefinitions().getKeyspace(0)
    actualKeyspace should equal(expectedKeyspace)
  }


  //
  //
  // table name
  //
  //

  test("Not priming table name should return empty table names") {

    // given
    val whenQuery = "select * from people"
    val rows: List[Map[String, String]] = List(Map("name" -> "Chris", "age" -> "19"))
    val columnTypes  = Map("name" -> CqlVarchar)
    prime(When(whenQuery), rows, "success", columnTypes)

    // when
    val result = session.execute(whenQuery)

    // then
    val actualTable = result.getColumnDefinitions().getTable(0)
    actualTable should equal("")
  }

  test("Priming table name should return expected table names") {

    // given
    val whenQuery = "select * from people"
    val expectedTable = "mytable"
    val rows: List[Map[String, String]] = List(Map("name" -> "Chris", "age" -> "19"))
    val columnTypes  = Map("name" -> CqlVarchar)
    prime(When(whenQuery, table = Some(expectedTable)), rows, "success", columnTypes)

    // when
    val result = session.execute(whenQuery)

    // then
    val actualTable = result.getColumnDefinitions().getTable(0)
    actualTable should equal(expectedTable)
  }
}
