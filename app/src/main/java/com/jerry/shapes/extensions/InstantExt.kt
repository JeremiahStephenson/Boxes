package com.jerry.shapes.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val Instant.readableDateAndTime: String get() {
    val formatter =
        DateTimeFormatter
            .ofPattern("MM/dd/yyyy H:mm")
            .withZone(ZoneId.systemDefault())
    return formatter.format(this)
}
