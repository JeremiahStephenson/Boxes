package com.jerry.boxes.extensions

import android.graphics.Point

fun Point.isNotOutside(columns: Int, rows: Int) =
    x >= 0 && x <= (columns - 1) && y >= 0 && y <= (rows - 1)