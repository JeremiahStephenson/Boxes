package com.jerry.bit.shapes.util

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<T>(
    val items: List<T>,
)
