package com.jerry.shapes.util

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

fun ComponentActivity.setEdgeToEdgeConfig() {
    enableEdgeToEdge()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Force the 3-button navigation bar to be transparent
        window.isNavigationBarContrastEnforced = false
    }
}
