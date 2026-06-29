package com.jerry.shapes.extensions

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RectF
import com.godaddy.android.colorpicker.HsvColor
import com.jerry.shapes.cache.data.ColorAndShape

val HsvColor.asColorAndShape get() =
    ColorAndShape(this.toColor())

fun Bitmap.findDominateColor(region: RectF): Int {
    val pixels = IntArray(region.width().toInt() * region.height().toInt())

    getPixels(pixels, 0, region.width().toInt(), region.left.toInt(), region.top.toInt(), region.width().toInt(), region.height().toInt())

    val colorMap: MutableList<HashMap<Int, Int>> = ArrayList()
    colorMap.add(HashMap())
    colorMap.add(HashMap())
    colorMap.add(HashMap())
    colorMap.add(HashMap())

    var color: Int
    var r: Int
    var g: Int
    var b: Int
    var a: Int
    var rC: Int?
    var gC: Int?
    var bC: Int?
    var bA: Int?
    for (i in pixels.indices) {
        color = pixels[i]
        r = Color.red(color)
        g = Color.green(color)
        b = Color.blue(color)
        a = Color.alpha(color)
        rC = colorMap[0][r]
        if (rC == null) rC = 0
        colorMap[0][r] = ++rC
        gC = colorMap[1][g]
        if (gC == null) gC = 0
        colorMap[1][g] = ++gC
        bC = colorMap[2][b]
        if (bC == null) bC = 0
        colorMap[2][b] = ++bC
        bA = colorMap[3][a]
        if (bA == null) bA = 0
        colorMap[3][a] = ++bA
    }

    val rgb = IntArray(4)
    for (i in 0..3) {
        var max = 0
        var colorRgb = 0
        for ((key, value) in colorMap[i]) {
            if (value > max) {
                max = value
                colorRgb = key
            }
        }
        rgb[i] = colorRgb
    }

    return Color.argb(rgb[3], rgb[0], rgb[1], rgb[2])
}
