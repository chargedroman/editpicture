package com.r.picturechargingedit.drawers

import android.graphics.*
import com.r.picturechargingedit.arch.Drawer
import com.r.picturechargingedit.model.crop.Crop
import com.r.picturechargingedit.mvp.impl.EditPictureViewArgs
import kotlin.math.min

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
    private val iconPaint = Paint()



    init {
        foregroundPaint.color = viewArgs.standardForegroundColor
        foregroundPaint.style = Paint.Style.FILL

        backgroundPaint.color = viewArgs.standardBackgroundColor
        backgroundPaint.style = Paint.Style.FILL

        borderPaint.color = viewArgs.accentColor
        borderPaint.style = Paint.Style.STROKE

        textPaint.color = viewArgs.accentColor

        iconPaint.colorFilter = PorterDuffColorFilter(viewArgs.accentColor, PorterDuff.Mode.SRC_IN)
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
        drawExpandIcon(canvas, croppingRect)
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

    private fun drawExpandIcon(canvas: Canvas, croppingRect: RectF) {
        val bitmap = viewArgs.iconExpand ?: return
        val minRect = min(croppingRect.height(), croppingRect.width())
        val minCanvas = min(canvas.height, canvas.width).coerceAtLeast(1)
        val factor = 1.5f * minRect/minCanvas
        val left = croppingRect.right - bitmap.width*factor
        val top = croppingRect.bottom - bitmap.height*factor
        canvas.drawBitmap(bitmap, left, top, iconPaint)
    }

}
