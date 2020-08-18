package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.r.picturechargingedit.EditPictureView
import java.util.*
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

    private var currentPath = BlurPath()
    private val allPaths = LinkedList<BlurPath>()

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
        for(path in allPaths) {
            canvas.drawPath(path, pathPaint)
        }
    }

    fun removeLastBlurPath() {
        if(allPaths.size > 0) {
            allPaths.removeLast()
            view.invalidate()
        }
    }

    fun startRecordingDraw(x: Float, y: Float) {
        currentPath = BlurPath()
        currentPath.moveTo(x, y)
        allPaths.add(currentPath)
        this.x = x
        this.y = y
        view.invalidate()
    }

    fun continueRecordingDraw(x: Float, y: Float) {
        val dx: Float = abs(x - this.x)
        val dy: Float = abs(y - this.y)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            currentPath.quadTo(this.x, this.y, (x + this.x)/2, (y + this.y)/2)
            this.x = x
            this.y = y
            view.invalidate()
        }
    }


}
