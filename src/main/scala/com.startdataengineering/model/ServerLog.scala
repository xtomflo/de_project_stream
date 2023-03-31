package com.startdataengineering.model

case class ServerLog(
                    eventId: String,
                    accountId: Int,
                    eventType: String,
                    locationCountry: String,
                    eventTimeStamp: Long
                    ) extends Serializable {
  override def toString: String = f"$eventId%s,$accountId%s,$eventType%s,$locationCountry%s,$eventTimeStamp%s"
}

object ServerLog {
  def fromString(value: String): Option[ServerLog] = {
    val elements: Array[String] = value.split(",")

    elements match {
      case Array(eventId, accountId, eventType, locationCountry, eventTimeStamp) =>
        try {
          Some(ServerLog(eventId, accountId.toInt, eventType, locationCountry, eventTimeStamp.toLong))
        } catch {
          case _: NumberFormatException => None
        }
      case _ => None
    }
  }
}