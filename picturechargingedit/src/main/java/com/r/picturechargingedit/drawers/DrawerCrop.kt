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

class DrawerCrop: Drawer<Crop>() {

    private val rectPaint = Paint()
    private val borderPaint = Paint()
    private val pointPaint = Paint()

    private val above: RectF = RectF()
    private val left: RectF = RectF()
    private val right: RectF = RectF()
    private val bottom: RectF = RectF()

    private val points =
        floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)


    init {
        rectPaint.color = Color.parseColor("#66000000")
        rectPaint.style = Paint.Style.FILL

        borderPaint.color = Color.parseColor("#10B6B7")
        borderPaint.style = Paint.Style.STROKE

        pointPaint.color = Color.parseColor("#10B6B7")
        pointPaint.style = Paint.Style.FILL
        pointPaint.strokeCap = Paint.Cap.ROUND
    }


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
        canvas.drawPoints(points, pointPaint)
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

    private fun setPoints(croppingRect: RectF) {
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
