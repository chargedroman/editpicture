package com.r.picturechargingedit.scale

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 *
 * Author: romanvysotsky
 * Created: 24.08.20
 */

@SuppressLint("ClickableViewAccessibility")
abstract class ScalingView : View, Scaling {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private val scalingHandler = ScalingHandler()
    private val interactionHandler = InteractionHandler()


    abstract fun onDrawScaled(canvas: Canvas)
    abstract fun onTouchScaled(type: Interaction, x: Float, y: Float)



    override fun setTranslateScaleEnabled(translate: Boolean, scale: Boolean) {
        scalingHandler.setTranslateScaleEnabled(translate, scale)
        interactionHandler.setTranslateScaleEnabled(translate, scale)
    }

    override fun setMinMaxScale(minScale: Float, maxScale: Float) {
        scalingHandler.setMinMaxScale(minScale, maxScale)
    }

    override fun setBoundsWidthHeight(width: Int, height: Int) {
        scalingHandler.setBoundsWidthHeight(width, height)
    }

    override fun getCurrentScalingFactor(): Float {
        return scalingHandler.getCurrentScalingFactor()
    }

    override fun getInvertedScalingMatrix(): Matrix {
        return scalingHandler.getInvertedScalingMatrix()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scalingHandler.setBoundsWidthHeight(width, height)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        scalingHandler.onTouchEvent(event)
        val matrix = scalingHandler.getInvertedScalingMatrix()
        interactionHandler.onTouchEvent(event, matrix, this::onTouchScaled)
        invalidate()
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            scalingHandler.onDraw(this)
            onDrawScaled(canvas)
            restore()
        }
    }

}
