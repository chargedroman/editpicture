package com.r.picturechargingedit.model.scaledevent

import android.graphics.Matrix
import android.view.MotionEvent
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ModeSettable
import com.r.picturechargingedit.model.scale.Scale
import com.r.picturechargingedit.model.scale.ScalingInteraction
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

class ScaleTouchModel(
    private val pictureModel: Picture,
    private val scalingModel: Scale
) : ModeSettable(), ScaleTouch {

    companion object {
        private const val SCALE_THRESHOLD = 400L
        private const val CLICK_THRESHOLD = 200L
        private const val DISTANCE_THRESHOLD = 100L
        private const val MOVE_AMOUNT_THRESHOLD = 3
    }

    private val mPointBuffer = floatArrayOf(0f, 0f)
    private var mMarginBuffer = 0f
    private var mLastTouchEventDownTime = 0L
    private var mLastTouchEventScaleTime = 0L
    private var mLastTouchEventDownX = 0f
    private var mLastTouchEventDownY = 0f
    private var mMoveEventCount = 0
    private var mStartedMoving = false


    override fun onTouchEvent(
        event: MotionEvent,
        onEventDetected: (ScalingMotionEvent) -> Unit
    ) {

        val matrix = scalingModel.getInvertedScalingMatrix()
        mapEvent(event, matrix)
        handleMoveEvent(event, onEventDetected)
        handleClickEvent(event, onEventDetected)

    }

    private fun mapEvent(event: MotionEvent, matrix: Matrix) {
        mPointBuffer[0] = event.x
        mPointBuffer[1] = event.y
        mMarginBuffer = matrix.mapRadius(pictureModel.getBitmapMargin())
        matrix.mapPoints(mPointBuffer)
    }

    private fun handleMoveEvent(event: MotionEvent, onEventDetected: (ScalingMotionEvent) -> Unit) {

        if(event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1) {
            mMoveEventCount = 0
            mStartedMoving = true
        }

        if(event.action == MotionEvent.ACTION_UP) {
            mMoveEventCount = 0
        }

        if(event.action == MotionEvent.ACTION_MOVE && event.pointerCount == 1) {
            mMoveEventCount++
        }

        if(event.action == MotionEvent.ACTION_MOVE && event.pointerCount > 1) {
            mLastTouchEventScaleTime = event.eventTime
        }

        if(event.isMove()) {
            if(mStartedMoving && !translatingEnabled) {
                onEventDetected(event.toScalingMotionEvent(ScalingInteraction.CLICK))
                mStartedMoving = false
            } else {
                onEventDetected(event.toScalingMotionEvent(ScalingInteraction.MOVE))
            }
        }

    }

    private fun handleClickEvent(event: MotionEvent, onEventDetected: (ScalingMotionEvent) -> Unit) {

        if(event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1) {
            mLastTouchEventDownTime = event.eventTime
            mLastTouchEventDownX = event.x
            mLastTouchEventDownY = event.y
        }

        if(event.isUp()) {
            onEventDetected(event.toScalingMotionEvent(ScalingInteraction.CLICK))
        }
    }

    private fun MotionEvent.toScalingMotionEvent(interaction: ScalingInteraction): ScalingMotionEvent {
        return ScalingMotionEvent(
            this,
            interaction,
            mPointBuffer[0],
            mPointBuffer[1],
            mMarginBuffer
        )
    }



    private fun MotionEvent.isMove(): Boolean {
        return action == MotionEvent.ACTION_MOVE
                && pointerCount == 1
                && mMoveEventCount > MOVE_AMOUNT_THRESHOLD
                && (eventTime - mLastTouchEventScaleTime) > SCALE_THRESHOLD
    }


    private fun MotionEvent.isUp(): Boolean {
        val clickTimeValid = (eventTime - mLastTouchEventDownTime) < CLICK_THRESHOLD
        val clickDistanceValid = distanceToOld() < DISTANCE_THRESHOLD
        return action == MotionEvent.ACTION_UP && pointerCount == 1
                && clickTimeValid
                && clickDistanceValid
    }


    private fun MotionEvent.distanceToOld(): Float {
        val x = x - mLastTouchEventDownX
        val y = y - mLastTouchEventDownY
        return kotlin.math.sqrt(x * x + y * y)
    }


}
