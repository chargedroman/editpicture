package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

interface Crop {

    fun onTouchEvent(event: ScalingMotionEvent)
    fun setMode(mode: EditPictureMode)
    fun clear()
    fun canDrawCrop(): Boolean

    fun getCroppingRect(): RectF
    fun getCroppingRectRadius(): Float

}
