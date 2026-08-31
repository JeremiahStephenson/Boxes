package com.jerry.bit.shapes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.jerry.bit.shapes.ui.theme.BoxesTheme
import com.jerry.bit.shapes.util.setEdgeToEdgeConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setEdgeToEdgeConfig()

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
