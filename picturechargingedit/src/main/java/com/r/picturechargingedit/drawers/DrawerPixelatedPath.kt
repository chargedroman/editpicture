package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.r.picturechargingedit.model.ChangesModel
import com.r.picturechargingedit.model.PathModel
import com.r.picturechargingedit.model.RectModel
import kotlin.math.sqrt

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerPixelatedPath(private val drawerArgs: DrawerArgs) {

    private var rectModelsToDraw = listOf<RectModel>()

    private val pathPaint = Paint()
    private var rectRadius = 0f
    private var rectPixelBuffer = IntArrayBuffer()


    init {
        pathPaint.color = Color.WHITE
        pathPaint.style = Paint.Style.FILL
        pathPaint.strokeCap = Paint.Cap.SQUARE
    }


    fun applyChanges(changes: ChangesModel, canvas: Canvas) {
        rectRadius = (canvas.width+canvas.height)/200f
        showPaths(changes)
        drawBlurPath(canvas)
    }


    fun showPaths(changes: ChangesModel) {
        rectModelsToDraw = changes.getPixelatedPaths().map { it.toRectModel() }
    }

    fun drawBlurPath(canvas: Canvas) {
        rectRadius = (canvas.width+canvas.height)/200f
        for(model in rectModelsToDraw) {
            for(rect in model.getRects()) {
                pathPaint.color = rect.getColor()
                canvas.drawRect(rect, pathPaint)
            }
        }
    }

    private fun PathModel.toRectModel(): RectModel {
        val model = RectModel()
        for(point in this.getPoints()) {
            model.add(point[0], point[1], rectRadius)
        }
        return model
    }

    private fun RectF.getColor(): Int {
        val width = this.width().toInt()
        val height = this.height().toInt()
        val centerX = this.centerX().toInt()
        val centerY = this.centerY().toInt()
        val pixelBuffer = rectPixelBuffer.get(width*height)

        drawerArgs.bitmap?.getPixels(pixelBuffer, 0, width, centerX, centerY, width, height)
        return calculateAverageColor(pixelBuffer)
    }

    private fun calculateAverageColor(pixels: IntArray): Int {
        var r = 0.0
        var g = 0.0
        var b = 0.0

        for(pixelColor in pixels) {
            r += Color.red(pixelColor) * Color.red(pixelColor)
            g += Color.green(pixelColor) * Color.green(pixelColor)
            b += Color.blue(pixelColor) * Color.blue(pixelColor)
        }

        return Color.rgb(
            sqrt(r/pixels.size).toInt(),
            sqrt(g/pixels.size).toInt(),
            sqrt(b/pixels.size).toInt()
        )
    }


    /**
     * to minimize object allocations during onDraw
     */
    private class IntArrayBuffer {

        private val map = mutableMapOf<Int, IntArray>()

        fun get(size: Int): IntArray {
            val array = map[size]

            if(array == null) {
                val a = IntArray(size)
                map[size] = a
                return a
            }

            return array
        }

    }

}
