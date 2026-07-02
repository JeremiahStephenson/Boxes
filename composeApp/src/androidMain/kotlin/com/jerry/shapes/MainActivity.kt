package com.jerry.shapes

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.jerry.shapes.ui.theme.BoxesTheme
import com.jerry.shapes.util.setEdgeToEdgeConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setEdgeToEdgeConfig()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Force the 3-button navigation bar to be transparent
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            BoxesTheme {
                MainContent { onBackPressedDispatcher.onBackPressed() }
            }
        }
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    moveTaskToBack(true)
                }
            },
        )
    }
}
