package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.r.picturechargingedit.arch.Drawer
import com.r.picturechargingedit.model.crop.Crop

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

abstract class BaseDrawerCrop: Drawer<Crop>() {

    private val rectPaint = Paint()
    private val borderPaint = Paint()
    private val pointPaint = Paint()

    private val above: RectF = RectF()
    private val left: RectF = RectF()
    private val right: RectF = RectF()
    private val bottom: RectF = RectF()


    init {
        rectPaint.color = Color.parseColor("#66000000")
        rectPaint.style = Paint.Style.FILL

        borderPaint.color = Color.parseColor("#10B6B7")
        borderPaint.style = Paint.Style.STROKE

        pointPaint.color = Color.parseColor("#10B6B7")
        pointPaint.style = Paint.Style.FILL
        pointPaint.strokeCap = Paint.Cap.ROUND
    }


    abstract fun setPoints(croppingRect: RectF)
    abstract fun drawPoints(canvas: Canvas, pointPaint: Paint)


    override fun drawChangesOnCanvas(changes: Crop, canvas: Canvas) {
        if(!changes.canDraw()) {
            return
        }

        pointPaint.strokeWidth = changes.getCroppingRectRadius()
        borderPaint.strokeWidth = changes.getCroppingRectRadius() / 2

        val croppingRect = changes.getCroppingRect()

        setRects(croppingRect, canvas)
        setPoints(croppingRect)

        canvas.drawRectsAround(croppingRect)
        drawPoints(canvas, pointPaint)
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
