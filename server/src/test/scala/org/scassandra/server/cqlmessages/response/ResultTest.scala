/*
 * Copyright (C) 2014 Christopher Batey and Dogan Narinc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scassandra.server.cqlmessages.response


import org.scalatest.{Matchers, FunSuite}
import akka.util.ByteString
import org.scassandra.server.cqlmessages._
import org.scassandra.server.cqlmessages.types._

class ResultTest extends FunSuite with Matchers {
  implicit val protocolVersion : ProtocolVersion = VersionOne

  test("test rows message from real cassandra") {
    val msg = ByteString(-126, 0, 0, 8,
      0, 0, 0, 81, // length
      0, 0, 0, 2, // Result type
      0, 0, 0, 1, // Flags
      0, 0, 0, 2, // Col count
      0, 6, // keyspace name length
      112, 101, 111, 112, 108, 101,
      0, 6, // table name length
      112, 101, 111, 112, 108, 101,
      0, 2, // col name length
      105, 100, // col name - id
      0, 15, // col type - timeuuid
      0, 10, // col name length
      102, 105, 114, 115, 116, 95, 110, 97, 109, 101, // col name - first_name
      0, 13, // col type - varchar
      0, 0, 0, 1, // row count
      0, 0, 0, 16, // length of uuid - id
      -96, 109, 9, 0, 0, 84, 17, -29, -127, -91, 103, 46, -8, -113, 21, -99,
      0, 0, 0, 5, // length of string
      99, 104, 114, 105, 115) // chris

    val response: Response = Result.fromByteString(msg)

    response.isInstanceOf[Rows] should equal(true)
    val rows = response.asInstanceOf[Rows]

    rows.keyspaceName should equal("people")
    rows.tableName should equal("people")
    rows.rows.length should equal(1)
    rows.columnTypes should equal(Map("id" -> CqlVarchar, "first_name" -> CqlVarchar))
    rows.rows.head.columns("first_name") should equal("chris")
    rows.rows.head.columns("id") should equal("a06d0900-0054-11e3-81a5-672ef88f159d")
  }

  test("SetKeyspace message from real Cassandra") {
    val setKeyspaceMsgFromCassandra = ByteString(-126, 0, 0, 8, 0, 0, 0, 12, 0, 0, 0, 3, 0, 6, 112, 101, 111, 112, 108, 101)

    val response = Result.fromByteString(setKeyspaceMsgFromCassandra)

    response.isInstanceOf[SetKeyspace] should equal(true)
    val setKeyspaceMsg = response.asInstanceOf[SetKeyspace]

    setKeyspaceMsg.keyspaceName should equal("people")
  }

  test("Void message de-serialize") {
    val streamId: Byte = 1

    val result = Result.fromByteString(VoidResult(streamId).serialize())

    result.isInstanceOf[VoidResult]
    result.asInstanceOf[VoidResult].stream should equal(streamId)
  }

  test("Serialisation of a void result") {
    val stream: Byte = 0x01
    val voidResult = VoidResult(stream)
    val bytes = voidResult.serialize().toList

    bytes should equal(List[Byte](
      protocolVersion.serverCode, // protocol version
      0x00, // flags
      stream, // stream
      0x08, // message type - 8 (Result)
      0x0, 0x0, 0x0, 0x4, // 4 byte integer - length (number of bytes)
      0x0, 0x0, 0x0, 0x1 // 4 byte integer for the type of result, 1 is a void result
    ))
  }

  test("Rows with a Ascii column type") {
    val msgWithAsciiColumn = createRowsWithColumnType(CqlAscii)

    val result = Result.fromByteString(msgWithAsciiColumn)

    result.isInstanceOf[Rows]
    val rows = result.asInstanceOf[Rows]
    rows.rows.length should equal(1)

    rows.rows(0).columns("id") should equal("hello")
  }

  test("Rows with a BigInt column type") {
    val msgWithAsciiColumn = createRowsWithColumnType(CqlBigint)

    val result = Result.fromByteString(msgWithAsciiColumn)

    result.isInstanceOf[Rows]
    val rows = result.asInstanceOf[Rows]
    rows.rows.length should equal(1)

    rows.rows(0).columns("id") should equal(555)
    rows.rows(0).columns("id").isInstanceOf[Long]
  }

  test("Rows with a Counter column type") {
    val msgWithAsciiColumn = createRowsWithColumnType(CqlCounter)

    val result = Result.fromByteString(msgWithAsciiColumn)

    result.isInstanceOf[Rows]
    val rows = result.asInstanceOf[Rows]
    rows.rows.length should equal(1)

    rows.rows(0).columns("id") should equal(1)
    rows.rows(0).columns("id").isInstanceOf[Long]
  }

  test("Rows with a Blob column type") {
    val msgWithAsciiColumn = createRowsWithColumnType(CqlBlob)

    val result = Result.fromByteString(msgWithAsciiColumn)

    result.isInstanceOf[Rows]
    val rows = result.asInstanceOf[Rows]
    rows.rows.length should equal(1)

    rows.rows(0).columns("id") should equal(Array(0, 0, 0, 0, 0, 0, 0, 3))
  }

  test("Rows with a Boolean column type") {
    val msgWithAsciiColumn = createRowsWithColumnType(CqlBoolean)

    val result = Result.fromByteString(msgWithAsciiColumn)

    result.isInstanceOf[Rows]
    val rows = result.asInstanceOf[Rows]
    rows.rows.length should equal(2)

    rows.rows(0).columns("id") should equal(false)
    rows.rows(1).columns("id") should equal(true)
  }

  test("Serialisation of a SetKeyspace result response") {
    val keyspaceName = "people"
    val stream: Byte = 0x02
    val setKeyspaceMessage = SetKeyspace(keyspaceName, stream)
    val bytes = setKeyspaceMessage.serialize()

    bytes should equal(List[Byte](
      protocolVersion.serverCode, // protocol version
      0x00, // flags
      stream, // stream
      0x08, // message type - 8 (Result)
      0x0, 0x0, 0x0, (keyspaceName.length + 6).toByte, // 4 byte integer - length (number of bytes)
      0x0, 0x0, 0x0, 0x3, // type of result - set_keyspace
      0x0, keyspaceName.size.toByte) :::
      keyspaceName.getBytes().toList)
  }


  private def createRowsWithColumnType(columnType: ColumnType[_]) = {
    var msgWithoutRows = ByteString(-126, 0, 0, 8,
      0, 0, 0, 81, // length
      0, 0, 0, 2, // Result type
      0, 0, 0, 1, // Flags
      0, 0, 0, 1, // Col count
      0, 6, // keyspace name length
      112, 101, 111, 112, 108, 101,
      0, 6, // table name length
      112, 101, 111, 112, 108, 101,
      0, 2, // col name length
      105, 100, // col name - id
      0, columnType.code.toByte, // col type

      0, 0, 0, 1) // row count

    val content: ByteString = columnType match {
      case CqlAscii => {
        ByteString(0, 0, 0, 5, // length of col value
          104, 101, 108, 108, 111) // "hello
      }
      case CqlBigint => {
        ByteString (0, 0, 0, 8, // length of col value
        0, 0, 0, 0, 0, 0, 2, 43) // col value
      }
      case CqlCounter => {
        ByteString (0, 0, 0, 8, // length of col value
          0, 0, 0, 0, 0, 0, 0, 1) // col value
      }
      case CqlBlob => {
        ByteString(0, 0, 0, 8, // length
        0, 0, 0, 0, 0, 0, 0, 3) // value
      }
      case CqlBoolean => {
        msgWithoutRows = msgWithoutRows.dropRight(4)
        msgWithoutRows = msgWithoutRows ++ ByteString(0,0,0,2)
        ByteString(0, 0, 0, 1, // length of first row
        0, // false
        0, 0, 0, 1, // length of second row
        1) // true

      }
    }

    msgWithoutRows ++ content
  }
}
