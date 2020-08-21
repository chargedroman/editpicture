package com.r.picturechargingedit.drawers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerBitmap(drawerArgs: DrawerArgs): Drawer<Bitmap>(drawerArgs) {

    private val src = RectF(0f, 0f, 0f, 0f)
    private val dest = RectF(0f, 0f, 0f, 0f)


    override fun drawChangesOnCanvas(changes: Bitmap, canvas: Canvas) {

        val pictureWidth = changes.width.toFloat()
        val pictureHeight = changes.height.toFloat()
        src.apply { right = pictureWidth; bottom = pictureHeight }

        val viewWidth = drawerArgs.getViewWidth().toFloat()
        val viewHeight = drawerArgs.getViewHeight().toFloat()
        dest.apply { right = viewWidth; bottom = viewHeight }

        drawerArgs.matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
        canvas.drawBitmap(changes, drawerArgs.matrix, null)
    }

}
