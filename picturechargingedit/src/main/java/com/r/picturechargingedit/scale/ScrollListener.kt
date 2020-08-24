package com.r.picturechargingedit.scale

import android.view.GestureDetector
import android.view.MotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 24.08.20
 */

class ScrollListener(val onScrolled: (Float, Float) -> Unit)
    : GestureDetector.SimpleOnGestureListener() {

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        val dX = e2.x - e1.x
        val dY = e2.y - e1.y
        onScrolled(dX, dY)
        return true
    }

}
