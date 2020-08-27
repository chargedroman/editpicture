package com.r.picturechargingedit.scale

import android.view.MotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

class ScalingMotionEvent(
    val original: MotionEvent,
    val interaction: ScalingInteraction,
    val mappedX: Float,
    val mappedY: Float
)
