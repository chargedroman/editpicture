package com.r.picturechargingedit.model.pixelation

import android.graphics.Color
import kotlin.math.sqrt

/**
 *
 * Author: romanvysotsky
 * Created: 25.08.21
 */

fun IntArray.calculateAverageColor(): Int {
    var r = 0.0
    var g = 0.0
    var b = 0.0

    for(pixelColor in this) {
        r += Color.red(pixelColor) * Color.red(pixelColor)
        g += Color.green(pixelColor) * Color.green(pixelColor)
        b += Color.blue(pixelColor) * Color.blue(pixelColor)
    }

    return Color.rgb(
        sqrt(r/this.size).toInt(),
        sqrt(g/this.size).toInt(),
        sqrt(b/this.size).toInt()
    )
}

fun IntArray.calculateAveragePixel(): Int {
    val averageColor = calculateAverageColor()
    val red = Color.red(averageColor)
    val green = Color.green(averageColor)
    val blue = Color.blue(averageColor)
    return (red+green+blue) / 3
}
