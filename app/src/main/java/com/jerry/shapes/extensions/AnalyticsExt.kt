package com.jerry.shapes.extensions

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

fun FirebaseAnalytics.logError(t: Throwable) {
    logEvent("SaveError") {
        param("Error", t.message.orEmpty())
    }
}
