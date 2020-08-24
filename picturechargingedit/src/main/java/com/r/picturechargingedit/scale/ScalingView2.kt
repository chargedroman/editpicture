package com.r.picturechargingedit.scale

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 * thanks https://judepereira.com/blog/multi-touch-in-android-translate-scale-and-rotate/
 * (modified source)
 *
 * Author: romanvysotsky
 * Created: 24.08.20
 */

@SuppressLint("ClickableViewAccessibility")
abstract class ScalingView2 : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    companion object {
        const val MIN_SCALE = 1f
        const val MAX_SCALE = 10f

        const val MIN_FINGER_DIST_FOR_ZOOM_EVENT = 10f
    }


    // these matrices will be used to move and zoom image
    private val mMatrix: Matrix = Matrix()
    private val mSavedMatrix: Matrix = Matrix()

    // we can be in one of these 3 states
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE

    // remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f


    abstract fun onDrawScaled(canvas: Canvas)
    abstract fun onTouchEventScaled(action: Int, x: Float, y: Float)


    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> onDown(event)
            MotionEvent.ACTION_POINTER_DOWN -> onPointerDown(event)
            MotionEvent.ACTION_MOVE -> onMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }

        invalidate()
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            setMatrix(mMatrix)
            onDrawScaled(canvas)
            restore()
        }
    }


    private fun onDown(event: MotionEvent) {
        mSavedMatrix.set(mMatrix)
        start[event.x] = event.y
        mode = DRAG
    }

    private fun onPointerDown(event: MotionEvent) {
        oldDist = spacing(event)
        if (oldDist > MIN_FINGER_DIST_FOR_ZOOM_EVENT) {
            mSavedMatrix.set(mMatrix)
            midPoint(mid, event)
            mode = ZOOM
        }
    }

    private fun onMove(event: MotionEvent) {
        if (mode == DRAG) {
            translate(event)
        } else if (mode == ZOOM) {
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
        if (newDist > 10f) {
            mMatrix.set(mSavedMatrix)
            val scale = newDist / oldDist
            mMatrix.postScale(scale, scale, mid.x, mid.y)
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


    private val valuesBuffer = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f ,0f)

    private fun getTotalScale(): Float {
        mMatrix.getValues(valuesBuffer)
        return valuesBuffer[0]
    }

}
