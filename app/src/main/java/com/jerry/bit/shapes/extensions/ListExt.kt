package com.jerry.bit.shapes.extensions

fun <T> MutableList<T>.addIfNotFound(item: T) {
    if (!contains(item)) {
        add(item)
    }
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> = filterValues { it != null } as Map<K, V>
