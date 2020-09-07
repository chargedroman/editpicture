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

class DrawerThumbnail(viewArgs: EditPictureViewArgs) : BaseDrawerCrop(viewArgs) {


    private val points =
        floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)


    override fun getCaptionString(): String = viewArgs.thumbCaption

    override fun drawPoints(canvas: Canvas, pointPaint: Paint) {
        canvas.drawPoints(points, pointPaint)
    }


    override fun setPoints(croppingRect: RectF) {

        points[0] = croppingRect.left
        points[1] = croppingRect.top

        points[2] = croppingRect.left
        points[3] = croppingRect.bottom

        points[4] = croppingRect.right
        points[5] = croppingRect.top

        points[6] = croppingRect.right
        points[7] = croppingRect.bottom

    }

}
