package com.r.picturechargingedit.model.scale

import android.graphics.Matrix
import android.view.MotionEvent
import com.r.picturechargingedit.EditPictureMode

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

interface Scale {

    fun onTouchEvent(event: MotionEvent)

    fun setMode(mode: EditPictureMode)
    fun setMinMaxScale(minScale: Float, maxScale: Float)

    fun getScalingMatrix(): Matrix
    fun getInvertedScalingMatrix(): Matrix

}
