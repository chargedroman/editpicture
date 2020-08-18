package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.r.picturechargingedit.EditPictureView
import kotlin.math.abs

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerBlurPath(private val view: EditPictureView) {

    companion object {
        private const val TOUCH_TOLERANCE = 4
    }

    private val path = Path()
    private val pathPaint = Paint()
    private var x: Float = 0f
    private var y: Float = 0f


    init {
        pathPaint.color = Color.WHITE
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeJoin = Paint.Join.ROUND
        pathPaint.strokeCap = Paint.Cap.ROUND
        pathPaint.strokeWidth = 20f
    }


    fun drawBlurPath(canvas: Canvas) {
        canvas.drawPath(path, pathPaint)
    }


    fun startRecordingDraw(x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y)
        this.x = x
        this.y = y
    }

    fun continueRecordingDraw(x: Float, y: Float) {
        val dx: Float = abs(x - this.x)
        val dy: Float = abs(y - this.y)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(this.x, this.y, (x + this.x)/2, (y + this.y)/2)
            this.x = x
            this.y = y
        }
    }

    fun completeRecordingDraw() {
        path.lineTo(x, y)
        view.invalidate()
    }

}
