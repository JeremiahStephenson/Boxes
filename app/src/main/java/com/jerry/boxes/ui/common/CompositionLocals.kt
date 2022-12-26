package com.jerry.boxes.ui.common

import androidx.compose.runtime.compositionLocalOf

val LocalAppBarTitle = compositionLocalOf<(String) -> Unit> { {} }