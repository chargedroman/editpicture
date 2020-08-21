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


    fun onDraw(canvas: Canvas) {
        val bitmap = drawerArgs.getCurrentBitmap() ?: return

        val pictureWidth = bitmap.width.toFloat()
        val pictureHeight = bitmap.height.toFloat()
        src.apply { right = pictureWidth; bottom = pictureHeight }

        val viewWidth = drawerArgs.getViewWidth().toFloat()
        val viewHeight = drawerArgs.getViewHeight().toFloat()
        dest.apply { right = viewWidth; bottom = viewHeight }

        drawerArgs.matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
        canvas.drawBitmap(bitmap, drawerArgs.matrix, null)
    }

}
