package com.jerry.shapes.extensions

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

fun FirebaseAnalytics.logError(t: Throwable) {
    logEvent("SaveError") {
        param("Error", t.message.orEmpty())
    }
}
