package com.jerry.shapes.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.reflect.KProperty

data class StateValue<T>(
    val value: T,
    val converter: T.() -> T = { this },
) {
    private var state by mutableStateOf(value)

    operator fun getValue(
        thisRef: Any,
        property: KProperty<*>,
    ) = state

    operator fun setValue(
        thisRef: Any,
        property: KProperty<*>,
        value: T,
    ) {
        state = value.converter()
    }
}
