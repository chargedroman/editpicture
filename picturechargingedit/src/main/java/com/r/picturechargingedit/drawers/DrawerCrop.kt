package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.r.picturechargingedit.mvp.impl.EditPictureViewArgs

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class DrawerCrop(viewArgs: EditPictureViewArgs) : BaseDrawerRect(viewArgs) {


    private val points =
        floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)


    override fun getCaptionString(): String = viewArgs.cropCaption

    override fun drawPoints(canvas: Canvas, pointPaint: Paint) {
        canvas.drawPoints(points, pointPaint)
    }


    override fun setPoints(croppingRect: RectF) {

        points[0] = croppingRect.left
        points[1] = croppingRect.top

        points[2] = croppingRect.left
        points[3] = croppingRect.centerY()

        points[4] = croppingRect.left
        points[5] = croppingRect.bottom

        points[6] = croppingRect.centerX()
        points[7] = croppingRect.top

        points[8] = croppingRect.centerX()
        points[9] = croppingRect.bottom

        points[10] = croppingRect.right
        points[11] = croppingRect.top

        points[12] = croppingRect.right
        points[13] = croppingRect.centerY()

        points[14] = croppingRect.right
        points[15] = croppingRect.bottom


    }


}
