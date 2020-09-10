package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import androidx.lifecycle.LiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

interface Crop {

    fun onTouchEvent(event: ScalingMotionEvent)
    fun hasChanges(): LiveData<Boolean>

    fun setMode(mode: EditPictureMode)
    fun setAspectRatio(aspectRatio: Float)
    fun setMinWidth(minWidth: Float)
    fun getAspectRatio(): Float

    fun clear()
    fun canDraw(): Boolean

    fun getCroppingRect(): RectF
    fun getCroppingRectRadius(): Float

}
