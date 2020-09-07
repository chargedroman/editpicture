package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.copyInto
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 * abstracts away updating [originalBoundsRect] and [croppingRectRadius]
 *
 * Author: romanvysotsky
 * Created: 07.09.20
 */

abstract class BaseBoundsCrop(val pictureModel: Picture): Crop {

    val originalBoundsRect: RectF = RectF()
    private var croppingRectRadius: Float = 1f
    private var currentMode: EditPictureMode = EditPictureMode.NONE
    private val bufferRect: RectF = RectF()


    abstract fun canDrawForMode(mode: EditPictureMode): Boolean
    abstract fun onBoundsUpdated()


    override fun getCroppingRectRadius(): Float = croppingRectRadius


    override fun clear() {
        originalBoundsRect.setEmpty()
        bufferRect.setEmpty()
    }

    override fun canDraw(): Boolean {
        updateBounds()
        return canDrawForMode(currentMode) && !getCroppingRect().isEmpty
    }

    override fun setMode(mode: EditPictureMode) {
        this.currentMode = mode
        updateBounds()
    }

    override fun onTouchEvent(event: ScalingMotionEvent) {
        updateBounds()
    }


    private fun updateBounds() {
        if(getCroppingRect().isEmpty) {
            originalBoundsRect.copyInto(getCroppingRect())
        }

        croppingRectRadius = pictureModel.getBitmapMargin()
        pictureModel.getBitmapBounds().copyInto(bufferRect)
        pictureModel.getMatrix().mapRect(bufferRect)
        bufferRect.copyInto(originalBoundsRect)
        onBoundsUpdated()
    }

}
