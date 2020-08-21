package com.r.picturechargingedit.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix

/**
 * keeps the bitmap and the matrix mapping the custom view coordinates to the bitmap's coordinates
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

class PictureModel {

    var bitmap: Bitmap? = null
    val matrix = Matrix()


    fun createCanvas(): Canvas? {
        val bitmap = bitmap ?: return null
        return Canvas(bitmap)
    }

    fun matrixInverted(): Matrix {
        val inverted = Matrix()
        matrix.invert(inverted)
        return inverted
    }

}
