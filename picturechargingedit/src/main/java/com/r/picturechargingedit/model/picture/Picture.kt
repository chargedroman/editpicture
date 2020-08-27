package com.r.picturechargingedit.model.picture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix

/**
 *
 * Author: romanvysotsky
 * Created: 25.08.20
 */

interface Picture {

    fun setBitmap(bitmap: Bitmap)
    fun getBitmap(): Bitmap?
    fun createBitmapCanvas(): Canvas?

    fun getMatrixInverted(): Matrix
    fun getMatrix(): Matrix

}
