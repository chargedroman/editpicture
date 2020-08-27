package com.r.picturechargingedit.model.scale.touch

import android.view.MotionEvent
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

interface ScaleTouch {

    fun setMode(mode: EditPictureMode)
    fun onTouchEvent(event: MotionEvent, onEventDetected: (ScalingMotionEvent) -> Unit)

}
