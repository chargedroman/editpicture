package com.r.picturechargingedit.scale

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.view.MotionEvent

/**
 *
 * thanks https://judepereira.com/blog/multi-touch-in-android-translate-scale-and-rotate/
 * (modified source)
 *
 * Author: romanvysotsky
 * Created: 24.08.20
 */

class ScalingHandler {

    companion object {
        const val MIN_SCALE = 1f
        const val MAX_SCALE = 10f
        const val MIN_FINGER_DIST_FOR_ZOOM_EVENT = 10f
    }

    private enum class Mode {
        NONE,
        TRANSLATE,
        SCALE
    }

    private var mode = Mode.NONE
    private var minScale = MIN_SCALE
    private var maxScale = MAX_SCALE
    private var boundsWidth = 0
    private var boundsHeight = 0

    // these matrices will be used to move and zoom image
    private val mMatrix: Matrix = Matrix()
    private val mMatrixInverted: Matrix = Matrix()
    private val mSavedMatrix: Matrix = Matrix()
    private val mMatrixBuffer = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f ,0f)

    // remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f


    fun setMinMaxScale(minScale: Float, maxScale: Float) {
        this.minScale = minScale
        this.maxScale = maxScale
    }

    fun setBoundsWidthHeight(width: Int, height: Int) {
        this.boundsWidth = width
        this.boundsHeight = height
    }

    fun getInvertedMatrix(): Matrix {
        mMatrix.invert(mMatrixInverted)
        return mMatrixInverted
    }

    fun getCurrentScalingFactor(): Float {
        mMatrix.getValues(mMatrixBuffer)
        return mMatrixBuffer[0]
    }


    fun onDraw(canvas: Canvas) {
        updateMatrixToNotDrawOutOfBounds(mMatrix)
        canvas.setMatrix(mMatrix)
    }


    fun onTouchEvent(event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> onDown(event)
            MotionEvent.ACTION_POINTER_DOWN -> onPointerDown(event)
            MotionEvent.ACTION_MOVE -> onMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = Mode.NONE
        }
    }


    private fun onDown(event: MotionEvent) {
        mSavedMatrix.set(mMatrix)
        start[event.x] = event.y
        mode = Mode.TRANSLATE
    }

    private fun onPointerDown(event: MotionEvent) {
        oldDist = spacing(event)
        if (oldDist > MIN_FINGER_DIST_FOR_ZOOM_EVENT) {
            mSavedMatrix.set(mMatrix)
            midPoint(mid, event)
            mode = Mode.SCALE
        }
    }

    private fun onMove(event: MotionEvent) {
        if (mode == Mode.TRANSLATE) {
            translate(event)
        } else if (mode == Mode.SCALE) {
            scale(event)
        }
    }

    private fun translate(event: MotionEvent) {
        mMatrix.set(mSavedMatrix)
        val dx = event.x - start.x
        val dy = event.y - start.y
        mMatrix.postTranslate(dx, dy)
    }

    private fun scale(event: MotionEvent) {
        val newDist = spacing(event)
        if (newDist > MIN_FINGER_DIST_FOR_ZOOM_EVENT) {

            mMatrix.set(mSavedMatrix)

            val scale = newDist / oldDist
            val currentScale = getCurrentScalingFactor()
            val nextScale = scale*currentScale
            val inAllowedScaleRange = nextScale in minScale..maxScale

            if(inAllowedScaleRange) {
                mMatrix.postScale(scale, scale, mid.x, mid.y)
            } else {
                val minMax = nextScale.coerceAtLeast(minScale).coerceAtMost(maxScale)
                val reverseScaleToMinMax = minMax/currentScale
                mMatrix.postScale(reverseScaleToMinMax, reverseScaleToMinMax, mid.x, mid.y)
            }

        }
    }


    /**
     * Determine the space between the first two fingers
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }


    /**
     * updates the matrix such that the image is not be translated outside of the view bounds
     */
    private fun updateMatrixToNotDrawOutOfBounds(matrix: Matrix) {
        matrix.getValues(mMatrixBuffer)

        val zoomFactor = mMatrixBuffer[0]
        val leastX = -zoomFactor*boundsWidth + boundsWidth
        val leastY = -zoomFactor*boundsHeight + boundsHeight
        mMatrixBuffer[2] = mMatrixBuffer[2].coerceAtMost(0f).coerceAtLeast(leastX)
        mMatrixBuffer[5] = mMatrixBuffer[5].coerceAtMost(0f).coerceAtLeast(leastY)

        matrix.setValues(mMatrixBuffer)
    }


}
