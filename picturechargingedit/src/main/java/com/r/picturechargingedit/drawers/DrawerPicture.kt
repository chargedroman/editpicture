package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import com.r.picturechargingedit.model.PictureModel

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerPicture(drawerArgs: DrawerArgs): Drawer<PictureModel>(drawerArgs) {

    private val src = RectF(0f, 0f, 0f, 0f)
    private val dest = RectF(0f, 0f, 0f, 0f)


    override fun drawChangesOnCanvas(changes: PictureModel, canvas: Canvas) {
        val bitmap = changes.bitmap ?: return

        val pictureWidth = bitmap.width.toFloat()
        val pictureHeight = bitmap.height.toFloat()
        src.apply { right = pictureWidth; bottom = pictureHeight }

        val viewWidth = drawerArgs.getViewWidth().toFloat()
        val viewHeight = drawerArgs.getViewHeight().toFloat()
        dest.apply { right = viewWidth; bottom = viewHeight }

        changes.matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
        canvas.drawBitmap(bitmap, changes.matrix, null)
    }

}