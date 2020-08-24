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
        private const val CLICK_THRESHOLD = 200L
        private const val DISTANCE_THRESHOLD = 100L
    }

    private val mPointBuffer = floatArrayOf(0f, 0f)
    private var mLastTouchEventDownTime = 0L
    private var mLastTouchEventDownX = 0f
    private var mLastTouchEventDownY = 0f


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
        if(event.action == MotionEvent.ACTION_MOVE) {
            onEventDetected(Interaction.MOVE, mPointBuffer[0], mPointBuffer[1])
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