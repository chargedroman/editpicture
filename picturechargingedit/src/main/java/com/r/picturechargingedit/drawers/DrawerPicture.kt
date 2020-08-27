package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import com.r.picturechargingedit.arch.Drawer
import com.r.picturechargingedit.model.picture.Picture

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerPicture: Drawer<Picture>() {

    private val src = RectF(0f, 0f, 0f, 0f)
    private val dest = RectF(0f, 0f, 0f, 0f)


    override fun drawChangesOnCanvas(changes: Picture, canvas: Canvas) {
        val bitmap = changes.getBitmap() ?: return
        val matrix = changes.getMatrix()

        val pictureWidth = bitmap.width.toFloat()
        val pictureHeight = bitmap.height.toFloat()
        src.apply { right = pictureWidth; bottom = pictureHeight }

        val viewWidth = canvas.width.toFloat()
        val viewHeight = canvas.height.toFloat()
        dest.apply { right = viewWidth; bottom = viewHeight }

        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
        canvas.drawBitmap(bitmap, matrix, null)
    }

}
