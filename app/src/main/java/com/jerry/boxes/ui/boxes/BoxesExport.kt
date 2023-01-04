package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.doOnLayout
import com.jerry.boxes.R
import com.jerry.boxes.util.storeImage
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// todo clean this up and add error handling
fun exportCanvas(
    scope: CoroutineScope,
    rootView: View,
    rows: Int,
    columns: Int,
    selections: Map<Point, SerializableColor?>
) {
    scope.launch(Dispatchers.Main) {
        (rootView as? ViewGroup)?.run {
            val composeView = ComposeView(context).apply {

                val newBoxes = generateBoxes(columns, rows, 100F, 0, 0)

                layoutParams = ViewGroup.LayoutParams(
                    columns * 100,
                    rows * 100
                )
                visibility = View.INVISIBLE

                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                id = R.id.imageExportId

                setContent {
                    val context = LocalContext.current
                    val captureController = rememberCaptureController()
                    Capturable(
                        controller = captureController,
                        onCaptured = { bitmap, error ->
                            // This is captured bitmap of a content inside Capturable Composable.
                            bitmap?.asAndroidBitmap()?.storeImage(context)
                            if (error != null) {
                                // Error occurred. Handle it!
                            }
                            rootView.removeView(this)
                        }
                    ) {
                        SelectionsBoxes(
                            scale = 1F,
                            offset = Offset.Zero,
                            boxes = newBoxes,
                            selections = selections
                        )
                    }
                    LaunchedEffect(Unit) {
                        doOnLayout {
                            captureController.capture()
                        }
                    }
                }
            }
            addView(composeView)
        }
    }
}