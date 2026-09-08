package com.jerry.bit.shapes.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Instant

private val MARKERS get() =
    DateFormatSymbols
        .getInstance(Locale.getDefault())
        .amPmStrings

private val DATE_TIME_CUSTOM_FORMAT =
    LocalDateTime.Format {
        monthNumber()
        char('/')
        day()
        char('/')
        year()
        char(' ')
        amPmHour(padding = Padding.NONE)
        char(':')
        minute()
        char(' ')
        amPmMarker(am = MARKERS[Calendar.AM], pm = MARKERS[Calendar.PM])
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
