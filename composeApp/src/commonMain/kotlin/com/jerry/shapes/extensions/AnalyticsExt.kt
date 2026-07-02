package com.jerry.shapes.extensions

import com.jerry.shapes.util.Analytics

fun Analytics.logError(t: Throwable) {
    logEvent("SaveError", mapOf("Error" to t.message.orEmpty()))
}
