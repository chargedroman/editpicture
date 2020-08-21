package com.r.picturechargingedit.drawers

import android.graphics.*
import com.r.picturechargingedit.model.RectPathModel
import kotlin.math.sqrt

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerPixelatedPath(private val drawerArgs: DrawerArgs) {

    private var rectPaths = listOf<RectPathModel>()

    private val pathPaint = Paint()
    private val pointBuffer = FloatArray(2)
    private val rectPixelBuffer = IntArrayBuffer()


    init {
        pathPaint.color = Color.WHITE
        pathPaint.style = Paint.Style.FILL
        pathPaint.strokeCap = Paint.Cap.SQUARE
    }


    fun drawChangesOnCanvas(changes: List<RectPathModel>, canvas: Canvas) {
        onDraw(changes, canvas, null)
    }


    fun showChanges(changes: List<RectPathModel>) {
        rectPaths = changes
    }

    fun onDraw(canvas: Canvas) {
        onDraw(rectPaths, canvas, drawerArgs.createInvertedMatrix())
    }


    private fun onDraw(rectPaths: List<RectPathModel>, canvas: Canvas, matrix: Matrix?) {
        for(model in rectPaths) {
            for(rect in model.getRects()) {
                pathPaint.color = rect.getColor(matrix)
                canvas.drawRect(rect, pathPaint)
            }
        }
    }


    private fun RectF.getCenter(matrix: Matrix?): FloatArray {
        pointBuffer[0] = this.centerX()
        pointBuffer[1] = this.centerY()

        return if(matrix == null) {
            pointBuffer
        } else {
            matrix.mapPoints(pointBuffer)
            pointBuffer
        }
    }

    private fun RectF.getColor(matrix: Matrix?): Int {
        val width = this.width().toInt()
        val height = this.height().toInt()
        val pointBuffer = this.getCenter(matrix)
        val centerX = pointBuffer[0].toInt()
        val centerY = pointBuffer[1].toInt()

        val pixelBuffer = try {
            val buffer = rectPixelBuffer.get(width*height)
            drawerArgs.bitmap?.getPixels(buffer, 0, width, centerX, centerY, width, height)
            buffer
        } catch (e: IllegalArgumentException) {
            return Color.TRANSPARENT
        }

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
