package com.r.picturechargingedit.scale

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
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
    private val mEventPoint = floatArrayOf(0f, 0f)

    private val mCanvasMatrix = Matrix()
    private val mCanvasMatrixInverted = Matrix()

    private val mScaleListener = ScaleListener {
        mScaleFactor *= it.scaleFactor
        mScaleFactor = 1f.coerceAtLeast(mScaleFactor.coerceAtMost(20f))

        mPivotPoint[0] = it.focusX
        mPivotPoint[1] = it.focusY

        mCanvasMatrixInverted.mapPoints(mPivotPoint)

        mCanvasMatrix.setScale(mScaleFactor, mScaleFactor, mPivotPoint[0], mPivotPoint[1])
        mCanvasMatrix.invert(mCanvasMatrixInverted)

        invalidate()
    }

    private val mScaleDetector = ScaleGestureDetector(context, mScaleListener)


    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)

        mEventPoint[0] = event.x
        mEventPoint[1] = event.y
        mCanvasMatrixInverted.mapPoints(mEventPoint)

        onTouchEvent(event.action, mEventPoint[0], mEventPoint[1])

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            setMatrix(mCanvasMatrix)
            onDrawContent(canvas)
            restore()
        }
    }

    abstract fun onDrawContent(canvas: Canvas)
    abstract fun onTouchEvent(action: Int, x: Float, y: Float)

}
