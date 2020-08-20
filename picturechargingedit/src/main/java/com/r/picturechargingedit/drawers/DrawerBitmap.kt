package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerBitmap(private val drawerArgs: DrawerArgs) {

    private val src = RectF(0f, 0f, 0f, 0f)
    private val dest = RectF(0f, 0f, 0f, 0f)


    fun drawPictureBitmap(canvas: Canvas) {
        val bitmap = drawerArgs.bitmap ?: return
        val matrix = drawerArgs.matrix

        val pictureWidth = bitmap.width.toFloat()
        val pictureHeight = bitmap.height.toFloat()
        src.apply { right = pictureWidth; bottom = pictureHeight }

        val viewWidth = drawerArgs.getOriginalViewWidth().toFloat()
        val viewHeight = drawerArgs.getOriginalViewHeight().toFloat()
        dest.apply { right = viewWidth; bottom = viewHeight }

        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
        canvas.drawBitmap(bitmap, matrix, null)
    }

}
