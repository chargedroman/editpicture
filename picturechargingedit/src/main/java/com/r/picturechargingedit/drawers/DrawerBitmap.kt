package com.r.picturechargingedit.drawers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import com.r.picturechargingedit.v2.EditPictureView

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerBitmap(private val view: EditPictureView) {

    var pictureBitmap: Bitmap? = null

    private val matrix = Matrix()
    private val src = RectF(0f, 0f, 0f, 0f)
    private val dest = RectF(0f, 0f, 0f, 0f)


    fun showBitmap(bitmap: Bitmap) {
        this.pictureBitmap = bitmap
        view.invalidate()
    }

    fun drawPictureBitmap(canvas: Canvas) {
        val bitmap = pictureBitmap ?: return

        val pictureWidth = bitmap.width.toFloat()
        val pictureHeight = bitmap.height.toFloat()
        src.apply { right = pictureWidth; bottom = pictureHeight }

        val viewWidth = view.width.toFloat()
        val viewHeight = view.height.toFloat()
        dest.apply { right = viewWidth; bottom = viewHeight }

        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
        canvas.drawBitmap(bitmap, matrix, null)
    }

}
