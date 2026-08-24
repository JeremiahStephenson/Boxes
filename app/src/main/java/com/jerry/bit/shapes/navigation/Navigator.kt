package com.jerry.bit.shapes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import timber.log.Timber

class Navigator(
    private val initialKey: NavKey,
) {
    private lateinit var _backStack: NavBackStack<NavKey>

    val backStack
        get() =
            when (::_backStack.isInitialized) {
                true ->
                    _backStack.also {
                        Timber.d("NavigationState: ${it.joinToString(", ")}")
                    }

                else -> NavBackStack(initialKey)
            }

    @Composable
    fun Init() {
        _backStack = rememberNavBackStack(initialKey)
    }

    @Composable
    fun entries() =
        _backStack.also {
            Timber.d("NavigationState: ${it.joinToString(", ")}")
        }

    fun navigate(
        navKey: NavKey,
        builder: Navigator.() -> Unit = {},
    ) = ensureBackStackIsInitialized {
        builder()
        _backStack.add(navKey)
    }

    fun popUpTo(
        navKey: Class<out NavKey>,
        inclusive: Boolean = true,
    ) = ensureBackStackIsInitialized {
        val index = _backStack.indexOfLast { navKey.isInstance(it) }
        val targetIndex = if (inclusive) index - 1 else index
        if (index != -1 && targetIndex >= 0) {
            repeat(_backStack.size - (targetIndex + 1)) {
                _backStack.removeLastOrNull()
            }
        }
    }

    fun popBackstack() =
        ensureBackStackIsInitialized {
            _backStack.removeLastOrNull()
        }

    private fun ensureBackStackIsInitialized(block: () -> Unit) {
        if (::_backStack.isInitialized) block()
    }

    val isAtRoot get() = _backStack.size == 1
}
