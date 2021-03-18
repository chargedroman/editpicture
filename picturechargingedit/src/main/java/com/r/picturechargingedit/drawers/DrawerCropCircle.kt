package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.r.picturechargingedit.arch.Drawer
import com.r.picturechargingedit.model.crop.Crop
import com.r.picturechargingedit.mvp.impl.EditPictureViewArgs

/**
 *
 * Author: romanvysotsky
 * Created: 18.03.21
 */

class DrawerCropCircle(viewArgs: EditPictureViewArgs): Drawer<Crop>(viewArgs) {


    private val foregroundPaint = Paint()
    private val backgroundPaint = Paint()
    private val borderPaint = Paint()
    private val textPaint = Paint()



    init {
        foregroundPaint.color = viewArgs.standardForegroundColor
        foregroundPaint.style = Paint.Style.FILL

        backgroundPaint.color = viewArgs.standardBackgroundColor
        backgroundPaint.style = Paint.Style.FILL

        borderPaint.color = viewArgs.accentColor
        borderPaint.style = Paint.Style.STROKE

        textPaint.color = viewArgs.accentColor
    }


    override fun drawChangesOnCanvas(changes: Crop, canvas: Canvas) {
        if(!changes.canDraw()) {
            return
        }

        val textSize = changes.getCroppingRectRadius() * 2
        borderPaint.strokeWidth = changes.getCroppingRectRadius() / 2
        textPaint.textSize = textSize

        val croppingRect = changes.getCroppingRect()

        drawCroppingRectAsCircle(canvas, croppingRect)
        canvas.drawText(viewArgs.cropCaption, croppingRect.left + textSize, croppingRect.top + textSize * 2, textPaint)
    }


    private fun drawCroppingRectAsCircle(canvas: Canvas, rect: RectF) {
        val cx = rect.centerX()
        val cy = rect.centerY()
        val radius = minOf((rect.height() / 2), (rect.width() / 2))
        canvas.drawRect(canvas.clipBounds, backgroundPaint)
        canvas.drawCircle(cx, cy, radius, foregroundPaint)
        canvas.drawCircle(cx, cy, radius, borderPaint)
    }

}
