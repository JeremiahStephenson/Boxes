package com.jerry.shapes.util

interface Analytics {
    fun logEvent(name: String, params: Map<String, Any?> = emptyMap())
    fun logError(throwable: Throwable)
}
