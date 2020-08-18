package com.r.picturechargingedit.drawers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import com.r.picturechargingedit.EditPictureView

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerBitmap(private val view: EditPictureView) {

    private var pictureBitmap: Bitmap? = null

    private val matrix = Matrix()
    private val src = RectF(0f, 0f, 0f, 0f)
    private val dest = RectF(0f, 0f, 0f, 0f)


    fun onNextBitmap(bitmap: Bitmap) {
        this.pictureBitmap = bitmap
        view.invalidate()
    }

    fun drawPictureBitmap(canvas: Canvas) {
        val bitmap = pictureBitmap ?: return

        src.apply {
            right = bitmap.width.toFloat()
            bottom = bitmap.height.toFloat()
        }

        dest.apply {
            right = view.width.toFloat()
            bottom = view.height.toFloat()
        }

        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
        canvas.drawBitmap(bitmap, matrix, null)
    }

}
