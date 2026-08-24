package com.jerry.bit.shapes.extensions

fun <T> lazyFast(operation: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) {
        operation()
    }

inline fun <T1 : Any, T2 : Any> safeLet(
    p1: T1?,
    p2: T2?,
    nullBlock: () -> Unit = {},
    block: (T1, T2) -> Unit,
): Unit = if (p1 != null && p2 != null) block(p1, p2) else nullBlock()

inline fun <T1 : Any, T2 : Any, T3 : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    nullBlock: () -> Unit = {},
    block: (T1, T2, T3) -> Unit,
): Unit = if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else nullBlock()

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    nullBlock: () -> Unit = {},
    block: (T1, T2, T3, T4) -> Unit,
): Unit = if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else nullBlock()

inline fun <T1 : Any, T2 : Any, T : Any?> safeReturn(
    p1: T1?,
    p2: T2?,
    block: (T1, T2) -> T?,
): T? = if (p1 != null && p2 != null) block(p1, p2) else null

inline fun <T1 : Any, T2 : Any, T3 : Any, T : Any?> safeReturn(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    block: (T1, T2, T3) -> T?,
): T? = if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T : Any?> safeReturn(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    block: (T1, T2, T3, T4) -> T?,
): T? = if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null

inline fun <T : Any> T?.ifNullElse(
    nullBlock: () -> Unit,
    block: T.() -> Unit,
) = when (this) {
    null -> nullBlock()
    else -> this.block()
}

val <T> T?.asList get() = this?.let { listOf(it) } ?: emptyList()
