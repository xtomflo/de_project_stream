package com.startdataengineering

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.TimeZone

import com.startdataengineering.model.ServerLog

class ServerLogSink extends RichSinkFunction[String] {

  private val INSERT_CASE = """
      |INSERT INTO server_log (eventId, userId, eventType, locationCountry, eventTimeStamp)
      |  VALUES (?, ?, ?, ?, ?)
      |  ON CONFLICT (eventId) DO UPDATE SET
      |    userId = excluded.userId,
      |    eventType = excluded.eventType,
      |    locationCountry = excluded.locationCountry,
      |    eventTimeStamp = excluded.eventTimeStamp
    """.stripMargin

  private val COUNTRY_MAP = Map(
    "USA" -> "United States of America",
    "IN" -> "India", "UK" -> "United Kingdom", "CA" -> "Canada",
    "AU" -> "Australia", "DE" -> "Germany", "ES" -> "Spain",
    "FR" -> "France", "NL" -> "New Zealand", "SG" -> "Singapore",
    "RU" -> "Russia", "JP" -> "Japan", "BR" -> "Brazil", "CN" -> "China",
    "O" -> "Other")

  private val dtFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
  dtFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

  private var stmt: PreparedStatement = _
  private var conn: Connection = _
  private var batchSize: Int = 0

  @throws[Exception]
  override def invoke(entity: String, context: SinkFunction.Context): Unit = {
    val sl = ServerLog.fromString(entity)

    sl.foreach { log =>
      stmt.setString(1, log.eventId)
      stmt.setInt(2, log.accountId)
      stmt.setString(3, log.eventType)
      stmt.setString(4, COUNTRY_MAP.getOrElse(log.locationCountry, "Other"))
      stmt.setString(5, dtFormat.format(log.eventTimeStamp * 1000L))
      stmt.addBatch()
    }
    batchSize = batchSize + 1

    // write to DB once we have 10k events accumulated
    if(batchSize >= 10000) {
      try {
        stmt.executeBatch()
      } catch {
        case e: Exception => e.printStackTrace()
          // Log the error or handle it as needed
      }
      batchSize = 0
    }

  }

  override def open(parameters: Configuration): Unit = {
    Class.forName("org.postgresql.Driver")

    conn = DriverManager.getConnection("jdbc:postgresql://postgres:5432/events?user=startdataengineer&password=password")
    stmt = conn.prepareStatement(INSERT_CASE)
  }

  @throws[Exception]
  override def close(): Unit = {
    conn.close()
  }
}