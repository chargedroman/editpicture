package com.r.picturechargingedit.scale

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
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


    private val scalingHandler = ScalingHandler()
    private val interactionHandler = InteractionHandler()


    abstract fun onDrawScaled(canvas: Canvas)
    abstract fun onTouchScaled(type: Interaction, x: Float, y: Float)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scalingHandler.setBoundsWidthHeight(width, height)
    }

    fun setMinMaxScale(minScale: Float, maxScale: Float) {
        scalingHandler.setMinMaxScale(minScale, maxScale)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        scalingHandler.onTouchEvent(event)

        val matrix = scalingHandler.getInvertedMatrix()
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
