package com.r.picturechargingedit.model.pixelation

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import com.r.picturechargingedit.model.PixelAverage
import com.r.picturechargingedit.util.IntArrayBuffer

/**
 * calculates and stores colors for a [RectColorModel] in an indexed manner
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

class RectColorModel(private val rectPathModel: RectPathModel):
    RectColor {

    private val pointBuffer = FloatArray(2)
    private val rectPixelBuffer = IntArrayBuffer()

    private val rectColors = mutableListOf<Int>()


    override fun getRectPathModel(): RectPath = rectPathModel
    override fun getColors(): List<Int> = rectColors


    fun calculateColors(bitmap: Bitmap, matrix: Matrix) {
        if(hasCalculatedColors()) return

        val rects = rectPathModel.getRects()

        for(i in rectColors.size until rects.size) {
            rectColors.add(rects[i].getColor(bitmap, matrix))
        }
    }


    private fun hasCalculatedColors(): Boolean {
        return rectColors.size == rectPathModel.getRects().size
    }


    private fun RectF.getColor(bitmap: Bitmap, matrix: Matrix): Int {
        val width = this.width().toInt()
        val height = this.height().toInt()
        val pointBuffer = this.getCenter(matrix)
        val centerX = pointBuffer[0].toInt()
        val centerY = pointBuffer[1].toInt()

        val pixelBuffer = try {
            val buffer = rectPixelBuffer.get(width*height)
            bitmap.getPixels(buffer, 0, width, centerX, centerY, width, height)
            buffer
        } catch (e: IllegalArgumentException) {
            return Color.TRANSPARENT
        }

        return PixelAverage(pixelBuffer).averageColor
    }

    private fun RectF.getCenter(matrix: Matrix): FloatArray {
        pointBuffer[0] = this.centerX()
        pointBuffer[1] = this.centerY()
        matrix.mapPoints(pointBuffer)
        return pointBuffer
    }

}
