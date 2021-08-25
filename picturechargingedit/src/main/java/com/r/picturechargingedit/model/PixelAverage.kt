package com.r.picturechargingedit.model

import android.graphics.Color
import kotlin.math.sqrt

/**
 * Stats about the average of pixels for an IntArray of pixels.
 *
 * Author: romanvysotsky
 * Created: 25.08.21
 */

class PixelAverage(pixelArray: IntArray) {

    val averageColor: Int
    val averagePixel: Int
    val averageRed: Int
    val averageGreen: Int
    val averageBlue: Int


    init {
        averageColor = calculateAverageColor(pixelArray)
        averageRed = Color.red(averageColor)
        averageGreen = Color.green(averageColor)
        averageBlue = Color.blue(averageColor)
        averagePixel = (averageRed+averageGreen+averageBlue) / 3
    }


    private fun calculateAverageColor(pixelArray: IntArray): Int {
        var r = 0.0
        var g = 0.0
        var b = 0.0

        for(pixelColor in pixelArray) {
            r += Color.red(pixelColor) * Color.red(pixelColor)
            g += Color.green(pixelColor) * Color.green(pixelColor)
            b += Color.blue(pixelColor) * Color.blue(pixelColor)
        }

        return Color.rgb(
            sqrt(r/pixelArray.size).toInt(),
            sqrt(g/pixelArray.size).toInt(),
            sqrt(b/pixelArray.size).toInt()
        )
    }


    /**
     * @return whether the picture is bright enough, considering th [brightnessThreshold]
     */
    fun isBright(brightnessThreshold: Int = 85): Boolean? {
        if(brightnessThreshold < 0 || brightnessThreshold > 255) {
            return null
        }

        return averagePixel >= brightnessThreshold
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PixelAverage) return false

        if (averageColor != other.averageColor) return false
        if (averagePixel != other.averagePixel) return false
        if (averageRed != other.averageRed) return false
        if (averageGreen != other.averageGreen) return false
        if (averageBlue != other.averageBlue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = averageColor
        result = 31 * result + averagePixel
        result = 31 * result + averageRed
        result = 31 * result + averageGreen
        result = 31 * result + averageBlue
        return result
    }

    override fun toString(): String {
        return "PixelAverage(averageColor=$averageColor, averagePixel=$averagePixel, " +
                "averageRed=$averageRed, averageGreen=$averageGreen, averageBlue=$averageBlue)"
    }

}
