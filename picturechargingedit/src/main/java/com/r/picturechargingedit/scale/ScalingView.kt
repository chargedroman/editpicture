package com.r.picturechargingedit.scale

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

/**
 *
 * Author: romanvysotsky
 * Created: 24.08.20
 */

@SuppressLint("ClickableViewAccessibility")
abstract class ScalingView: View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private var mScaleFactor = 1f

    private val mPivotPoint  = floatArrayOf(0f, 0f)
    private val mTranslatePoint  = floatArrayOf(0f, 0f)
    private val mEventPoint = floatArrayOf(0f, 0f)

    private val mCanvasMatrix = Matrix()
    private val mCanvasMatrixInverted = Matrix()

    private val mScaleListener = ScaleListener { onScale(it) }
    private val mGestureListener = ScrollListener { x, y -> onScrolled(x, y) }

    private val mScaleDetector = ScaleGestureDetector(context, mScaleListener)
    private val mGestureDetector = GestureDetector(context, mGestureListener)


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val result = mScaleDetector.onTouchEvent(event) or
                mGestureDetector.onTouchEvent(event)

        mEventPoint[0] = event.x
        mEventPoint[1] = event.y
        mCanvasMatrixInverted.mapPoints(mEventPoint)

        onTouchEventScaled(event.action, mEventPoint[0], mEventPoint[1])

        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            setMatrix(mCanvasMatrix)
            onDrawScaled(canvas)
            restore()
        }
    }

    abstract fun onDrawScaled(canvas: Canvas)
    abstract fun onTouchEventScaled(action: Int, x: Float, y: Float)


    private fun onScale(it: ScaleGestureDetector) {
        mScaleFactor *= it.scaleFactor
        mScaleFactor = 1f.coerceAtLeast(mScaleFactor.coerceAtMost(10f))

        mPivotPoint[0] = it.focusX
        mPivotPoint[1] = it.focusY

        mCanvasMatrixInverted.mapPoints(mPivotPoint)

        updateMatrices()
        invalidate()
    }

    private fun onScrolled(x: Float, y: Float) {

        if(mScaleDetector.isInProgress) {
            return
        }

        mTranslatePoint[0] = x
        mTranslatePoint[1] = y

        updateMatrices()
        invalidate()
    }

    private fun updateMatrices() {
        mCanvasMatrix.setScale(mScaleFactor, mScaleFactor, mPivotPoint[0], mPivotPoint[1])
        mCanvasMatrix.postTranslate(mTranslatePoint[0], mTranslatePoint[1])
        mCanvasMatrix.invert(mCanvasMatrixInverted)
    }

}
