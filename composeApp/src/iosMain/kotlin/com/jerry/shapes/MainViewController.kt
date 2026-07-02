package com.jerry.shapes

import androidx.compose.ui.window.ComposeUIViewController
import com.jerry.shapes.inject.appModule
import com.jerry.shapes.ui.theme.BoxesTheme
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule)
    }
    BoxesTheme {
        MainContent(onBackPressed = {})
    }
}
