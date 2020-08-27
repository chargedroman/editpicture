package com.r.picturechargingedit.model.picture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF

/**
 *
 * Author: romanvysotsky
 * Created: 25.08.20
 */

interface Picture {

    fun setBitmap(bitmap: Bitmap)

    fun getBitmap(): Bitmap?
    fun getBitmapMargin(): Float

    fun getBitmapBounds(): RectF
    fun getBitmapBoundsMapped(): RectF

    fun getMatrixInverted(): Matrix
    fun getMatrix(): Matrix

    fun createBitmapCanvas(): Canvas?

}
