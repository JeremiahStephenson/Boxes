package com.jerry.boxes.ui.layers

import android.graphics.Bitmap

data class LayerEditUi(
    val id: Long,
    val index: Int,
    val name: String,
    val image: Bitmap
)
