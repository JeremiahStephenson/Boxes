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
import com.jerry.boxes.ui.boxes.data.LayerUi
import com.jerry.boxes.util.CoroutineContextProvider
import com.jerry.boxes.util.storeImage
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

// todo clean this up and add error handling
fun exportCanvas(
    rootView: View,
    projectId: Long,
    export: Boolean,
    rows: Int,
    columns: Int,
    imageSize: Float,
    layers: List<LayerUi>,
    selections: Map<Long, Map<Point, ColorAndShape>>,
    cc: CoroutineContextProvider
) {
    (rootView as? ViewGroup)?.run {
        val composeView = ComposeView(context).apply {

            val boxSize = max(imageSize / columns.toFloat(), imageSize / rows.toFloat())

            val newBoxes = generateBoxes(columns, rows, boxSize.roundToInt().toFloat(), 0F, 0F)

            layoutParams = ViewGroup.LayoutParams(
                columns * boxSize.roundToInt(),
                rows * boxSize.roundToInt()
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
                            bitmap?.asAndroidBitmap()?.storeImage(context, export, projectId.toString())
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