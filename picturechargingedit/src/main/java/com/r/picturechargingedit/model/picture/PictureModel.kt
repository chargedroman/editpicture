package com.r.picturechargingedit.model.picture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import com.r.picturechargingedit.model.Picture

/**
 * keeps the bitmap and the matrix mapping the custom view coordinates to the bitmap's coordinates
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

class PictureModel: Picture {

    private var bitmap: Bitmap? = null
    private val matrix = Matrix()

    override fun getMatrix(): Matrix = matrix
    override fun getBitmap(): Bitmap? = bitmap

    override fun setBitmap(bitmap: Bitmap) {
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

}
