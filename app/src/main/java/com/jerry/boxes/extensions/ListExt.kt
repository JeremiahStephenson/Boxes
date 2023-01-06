package com.jerry.boxes.extensions

fun<T> MutableList<T>.addIfNotFound(item: T) {
    if (!contains(item)) { add(item) }
}