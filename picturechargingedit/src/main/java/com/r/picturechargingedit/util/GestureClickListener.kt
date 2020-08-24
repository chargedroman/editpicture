package com.r.picturechargingedit.util

import android.view.GestureDetector
import android.view.MotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 24.08.20
 */

class GestureClickListener(val onClick: (MotionEvent) -> Unit)
    : GestureDetector.SimpleOnGestureListener() {

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return super.onSingleTapConfirmed(e)
    }

}
