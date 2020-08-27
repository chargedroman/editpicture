package com.r.picturechargingedit.model.picture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF

/**
 * keeps the bitmap and the matrix mapping the custom view coordinates to the bitmap's coordinates
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

class PictureModel: Picture {

    companion object {
        private const val MARGIN_STANDARD = 20f
        private const val THOUSAND = 1000f
        private const val RELATIVE_RECT_MARGIN_FACTOR = 1.6f
    }


    private var bitmap: Bitmap? = null
    private var bitmapMargin: Float = MARGIN_STANDARD
    private val matrix = Matrix()
    private val bitmapBounds = RectF()
    private val bitmapBoundsMapped = RectF()


    override fun getBitmapBoundsMapped(): RectF = bitmapBoundsMapped
    override fun getBitmapBounds(): RectF = bitmapBounds
    override fun getBitmapMargin(): Float = bitmapMargin
    override fun getMatrix(): Matrix = matrix
    override fun getBitmap(): Bitmap? = bitmap


    override fun setBitmap(bitmap: Bitmap) {
        this.bitmapMargin = calculateBitmapMargin(bitmap).coerceAtLeast(MARGIN_STANDARD)
        this.bitmap = bitmap
    }

    override fun createBitmapCanvas(): Canvas? {
        val bitmap = bitmap ?: return null
        return Canvas(bitmap)
    }

    override fun getMatrixInverted(): Matrix {
        val inverted = Matrix()
        matrix.invert(inverted)
        return inverted
    }


    private fun calculateBitmapMargin(bitmap: Bitmap): Float {
        val width = bitmap.width/ THOUSAND
        val height = bitmap.height/ THOUSAND
        return width * height * RELATIVE_RECT_MARGIN_FACTOR
    }

}
