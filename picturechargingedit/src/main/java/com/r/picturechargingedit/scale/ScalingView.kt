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
abstract class ScalingView : View {

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


    private enum class Mode {
        NONE,
        TRANSLATE,
        SCALE
    }

    private var mode = Mode.NONE
    private var minScale = MIN_SCALE
    private var maxScale = MAX_SCALE


    // these matrices will be used to move and zoom image
    private val mMatrix: Matrix = Matrix()
    private val mMatrixInverted: Matrix = Matrix()
    private val mSavedMatrix: Matrix = Matrix()
    private val mMatrixBuffer = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f ,0f)
    private val mPointBuffer = floatArrayOf(0f, 0f)

    // remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f


    abstract fun onDrawScaled(canvas: Canvas)
    abstract fun onTouchEventScaled(action: Int, x: Float, y: Float)



    fun setMinMaxScale(minScale: Float, maxScale: Float) {
        this.minScale = minScale
        this.maxScale = maxScale
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> onDown(event)
            MotionEvent.ACTION_POINTER_DOWN -> onPointerDown(event)
            MotionEvent.ACTION_MOVE -> onMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = Mode.NONE
        }

        mapEventPoint(event)
        invalidate()
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            updateMatrixToNotDrawOutOfBounds(mMatrix)
            setMatrix(mMatrix)
            onDrawScaled(canvas)
            restore()
        }
    }


    private fun mapEventPoint(event: MotionEvent) {
        mPointBuffer[0] = event.x
        mPointBuffer[1] = event.y
        mMatrix.invert(mMatrixInverted)
        mMatrixInverted.mapPoints(mPointBuffer)
        onTouchEventScaled(event.action, mPointBuffer[0], mPointBuffer[1])
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
        updateMatrixToNotDrawOutOfBounds(mMatrix)
    }

    private fun scale(event: MotionEvent) {
        val newDist = spacing(event)
        if (newDist > MIN_FINGER_DIST_FOR_ZOOM_EVENT) {
            mMatrix.set(mSavedMatrix)
            val scale = newDist / oldDist
            mMatrix.postScale(scale, scale, mid.x, mid.y)
            updateMatrixToNotDrawOutOfBounds(mMatrix)
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



    private fun updateMatrixToNotDrawOutOfBounds(matrix: Matrix) {
        matrix.getValues(mMatrixBuffer)

        val zoomFactor = mMatrixBuffer[0]

        //scalex
        mMatrixBuffer[0] = zoomFactor.coerceAtLeast(minScale).coerceAtMost(maxScale)
        //scaley
        mMatrixBuffer[4] = zoomFactor.coerceAtLeast(minScale).coerceAtMost(maxScale)

        val leastX = -zoomFactor*width + width
        val leastY = -zoomFactor*height + height

        //translate x
        mMatrixBuffer[2] = mMatrixBuffer[2].coerceAtMost(0f).coerceAtLeast(leastX)
        //translate y
        mMatrixBuffer[5] = mMatrixBuffer[5].coerceAtMost(0f).coerceAtLeast(leastY)

        matrix.setValues(mMatrixBuffer)
    }


}
