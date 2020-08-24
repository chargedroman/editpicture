package com.r.picturechargingedit.scale

import android.view.ScaleGestureDetector

/**
 *
 * Author: romanvysotsky
 * Created: 24.08.20
 */

class ScaleListener(val onScaled: (ScaleGestureDetector) -> Unit)
    : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        onScaled(detector)
        return true
    }

}
