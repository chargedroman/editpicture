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
    }

    private val mPointBuffer = floatArrayOf(0f, 0f)
    private var lastTouchEventDownTime = 0L


    fun onTouchEvent(
        event: MotionEvent,
        matrix: Matrix,
        onEventDetected: (Interaction, Float, Float) -> Unit
    ) {

        mPointBuffer[0] = event.x
        mPointBuffer[1] = event.y
        matrix.mapPoints(mPointBuffer)

        if(event.action == MotionEvent.ACTION_MOVE) {
            onEventDetected(Interaction.MOVE, mPointBuffer[0], mPointBuffer[1])
        }

        if(event.action == MotionEvent.ACTION_DOWN && event.pointerCount == 1) {
            lastTouchEventDownTime = event.eventTime
        }

        if(event.action == MotionEvent.ACTION_UP && event.pointerCount == 1
            && (event.eventTime - lastTouchEventDownTime) < CLICK_THRESHOLD
        ) {
            onEventDetected(Interaction.CLICK, mPointBuffer[0], mPointBuffer[1])
        }

    }

}
