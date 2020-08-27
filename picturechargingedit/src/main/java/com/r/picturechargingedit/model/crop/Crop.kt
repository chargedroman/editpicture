package com.r.picturechargingedit.model.crop

import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

interface Crop {

    fun onTouchEvent(event: ScalingMotionEvent)

}
