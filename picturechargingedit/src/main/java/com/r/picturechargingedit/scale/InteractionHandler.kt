package com.r.picturechargingedit.scale

import android.graphics.Matrix
import android.view.MotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 24.08.20
 */

class InteractionHandler {

    companion object {
        private const val SCALE_THRESHOLD = 400L
        private const val CLICK_THRESHOLD = 200L
        private const val DISTANCE_THRESHOLD = 100L
        private const val MOVE_AMOUNT_THRESHOLD = 3
    }

    private val mPointBuffer = floatArrayOf(0f, 0f)
    private var mLastTouchEventDownTime = 0L
    private var mLastTouchEventScaleTime = 0L
    private var mLastTouchEventDownX = 0f
    private var mLastTouchEventDownY = 0f
    private var mMoveEventCount = 0
    private var mStartedMoving = false

    private var translatingEnabled = true
    private var scalingEnabled = true


    fun setTranslateScaleEnabled(translate: Boolean, scale: Boolean) {
        translatingEnabled = translate
        scalingEnabled = scale
    }

    fun onTouchEvent(
        event: MotionEvent,
        matrix: Matrix,
        onEventDetected: (Interaction, Float, Float) -> Unit
    ) {

        mapPoint(event, matrix)
        handleMoveEvent(event, onEventDetected)
        handleClickEvent(event, onEventDetected)

    }

    private fun mapPoint(event: MotionEvent, matrix: Matrix) {
        mPointBuffer[0] = event.x
        mPointBuffer[1] = event.y
        matrix.mapPoints(mPointBuffer)
    }


    private fun handleMoveEvent(event: MotionEvent, onEventDetected: (Interaction, Float, Float) -> Unit) {

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
                 onEventDetected(Interaction.CLICK, mPointBuffer[0], mPointBuffer[1])
                 mStartedMoving = false
             } else {
                 onEventDetected(Interaction.MOVE, mPointBuffer[0], mPointBuffer[1])
             }
        }

    }

    private fun handleClickEvent(event: MotionEvent, onEventDetected: (Interaction, Float, Float) -> Unit) {

        if(event.isDown()) {
            mLastTouchEventDownTime = event.eventTime
            mLastTouchEventDownX = event.x
            mLastTouchEventDownY = event.y
        }

        if(event.isUp()) {
            onEventDetected(Interaction.CLICK, mPointBuffer[0], mPointBuffer[1])
        }
    }


    private fun MotionEvent.isDown(): Boolean {
        return action == MotionEvent.ACTION_DOWN && pointerCount == 1
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
