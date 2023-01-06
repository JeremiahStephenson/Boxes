package com.jerry.boxes.ui.boxes

import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.doOnLayout
import com.jerry.boxes.R
import com.jerry.boxes.cache.data.Layer
import com.jerry.boxes.util.CoroutineContextProvider
import com.jerry.boxes.util.storeImage
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

// todo clean this up and add error handling
fun exportCanvas(
    rootView: View,
    rows: Int,
    columns: Int,
    layers: List<Layer>,
    selections: Map<Point, Map<Long, SerializableColor?>?>,
    cc: CoroutineContextProvider
) {
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
                val scope = rememberCoroutineScope()
                Capturable(
                    controller = captureController,
                    onCaptured = { bitmap, error ->
                        // This is captured bitmap of a content inside Capturable Composable.
                        scope.launch(cc.io) {
                            bitmap?.asAndroidBitmap()?.storeImage(context)
                            if (error != null) {
                                // Error occurred. Handle it!
                            }
                            scope.launch(cc.main) {
                                rootView.removeView(this@apply)
                            }
                        }
                    }
                ) {
                    SelectionsBoxes(
                        scale = 1F,
                        offset = Offset.Zero,
                        boxes = newBoxes,
                        selections = selections,
                        layers = layers
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