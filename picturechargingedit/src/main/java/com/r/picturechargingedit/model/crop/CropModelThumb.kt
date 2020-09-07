package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.copyInto
import com.r.picturechargingedit.arch.isZero
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class CropModelThumb(private val pictureModel: Picture) : Crop {

    private val originalBoundsRect: RectF = RectF()
    private val croppingRect: RectF = RectF()

    private var croppingRectRadius: Float = 1f
    private var aspectRatio: Float = 1f
    private var currentMode: EditPictureMode = EditPictureMode.NONE


    override fun onTouchEvent(event: ScalingMotionEvent) {
        println("foo")
    }

    override fun getCroppingRect(): RectF = croppingRect

    override fun getCroppingRectRadius(): Float = croppingRectRadius


    override fun setMode(mode: EditPictureMode) {
        this.currentMode = mode
    }

    override fun setAspectRatio(aspectRatio: Float) {
        this.aspectRatio = aspectRatio
    }

    override fun getAspectRatio(): Float {
        return aspectRatio
    }

    override fun clear() {
        originalBoundsRect.set(0f, 0f, 0f, 0f)
        originalBoundsRect.copyInto(croppingRect)
    }

    override fun canDraw(): Boolean {
        return currentMode == EditPictureMode.THUMBNAIL && !getCroppingRect().isZero()
    }


}
