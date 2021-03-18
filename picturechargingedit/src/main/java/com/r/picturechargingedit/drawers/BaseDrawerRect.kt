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
 * Created: 18.08.20
 */

abstract class BaseDrawerRect(viewArgs: EditPictureViewArgs): Drawer<Crop>(viewArgs) {


    private val rectPaint = Paint()
    private val borderPaint = Paint()
    private val pointPaint = Paint()
    private val textPaint = Paint()

    private val above: RectF = RectF()
    private val left: RectF = RectF()
    private val right: RectF = RectF()
    private val bottom: RectF = RectF()


    init {
        rectPaint.color = viewArgs.backgroundColor
        rectPaint.style = Paint.Style.FILL

        borderPaint.color = viewArgs.accentColor
        borderPaint.style = Paint.Style.STROKE

        pointPaint.color = viewArgs.accentColor
        pointPaint.style = Paint.Style.FILL
        pointPaint.strokeCap = Paint.Cap.ROUND

        textPaint.color = viewArgs.accentColor
    }


    abstract fun setPoints(croppingRect: RectF)
    abstract fun drawPoints(canvas: Canvas, pointPaint: Paint)
    abstract fun getCaptionString(): String


    override fun drawChangesOnCanvas(changes: Crop, canvas: Canvas) {
        if(!changes.canDraw()) {
            return
        }

        val textSize = changes.getCroppingRectRadius() * 2
        pointPaint.strokeWidth = changes.getCroppingRectRadius()
        borderPaint.strokeWidth = changes.getCroppingRectRadius() / 2
        textPaint.textSize = textSize

        val croppingRect = changes.getCroppingRect()

        setRects(croppingRect, canvas)
        setPoints(croppingRect)

        canvas.drawRectsAround(croppingRect)
        drawPoints(canvas, pointPaint)
        canvas.drawText(getCaptionString(), croppingRect.left + textSize, croppingRect.top + textSize * 2, textPaint)
    }


    private fun Canvas.drawRectsAround(croppingRect: RectF) {
        drawRect(above, rectPaint)
        drawRect(left, rectPaint)
        drawRect(right, rectPaint)
        drawRect(bottom, rectPaint)
        drawRect(croppingRect, borderPaint)
    }

    private fun setRects(croppingRect: RectF, canvas: Canvas) {
        above.set(0f, 0f, canvas.width.toFloat(), croppingRect.top)
        left.set(0f, croppingRect.top, croppingRect.left, croppingRect.bottom)
        right.set(croppingRect.right, croppingRect.top, canvas.width.toFloat(), croppingRect.bottom)
        bottom.set(0f, croppingRect.bottom, canvas.width.toFloat(), canvas.height.toFloat())
    }


}
