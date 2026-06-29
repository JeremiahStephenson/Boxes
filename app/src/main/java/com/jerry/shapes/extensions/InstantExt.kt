package com.jerry.shapes.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

private val DATE_TIME_CUSTOM_FORMAT =
    LocalDateTime.Format {
        monthNumber()
        char('/')
        day()
        char('/')
        year()
        char(' ')
        hour()
        char(':')
        minute()
    }

private val DATE_TIME_FILE_FORMAT =
    LocalDateTime.Format {
        day()
        monthNumber()
        year()
        char('_')
        hour()
        minute()
    }

val Instant.readableDateAndTime: String get() =
    toLocalDateTime(TimeZone.currentSystemDefault()).format(DATE_TIME_CUSTOM_FORMAT)

val currentFileTimeStamp get() =
    Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(DATE_TIME_FILE_FORMAT)
