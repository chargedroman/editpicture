package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import android.view.MotionEvent
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 * abstracts away the [onTouchEvent] to expose the conceptually easier [cropRectWith]
 *
 * Author: romanvysotsky
 * Created: 07.09.20
 */

abstract class BaseCrop(pictureModel: Picture) : BaseBoundsCrop(pictureModel) {

    private var aspectRatio: Float = 1f

    private val bufferRect: RectF = RectF()
    private val croppingRect: RectF = RectF()
    private var currentCropArea: CropArea = CropArea.NONE

    private var lastEvent: ScalingMotionEvent? = null


    abstract fun cropRectWith(
        area: CropArea,
        lastEvent: ScalingMotionEvent,
        currentEvent: ScalingMotionEvent
    )


    override fun setAspectRatio(aspectRatio: Float) {
        this.aspectRatio = aspectRatio
    }

    override fun getAspectRatio(): Float = aspectRatio
    override fun getCroppingRect(): RectF = croppingRect


    override fun clear() {
        super.clear()
        croppingRect.setEmpty()
        bufferRect.setEmpty()
        currentCropArea = CropArea.NONE
        lastEvent = null
    }


    override fun onTouchEvent(event: ScalingMotionEvent) {
        super.onTouchEvent(event)
        actDependingOnEventAction(event)
    }


    private fun actDependingOnEventAction(event: ScalingMotionEvent) {

        if(event.isDown()) {
            currentCropArea = calculateCurrentCropArea(event)
            if(currentCropArea != CropArea.NONE) {
                lastEvent = event
            }
        }

        if(event.isUp()) {
            currentCropArea = CropArea.NONE
            lastEvent = null
        }

        checkForMoveEvent(event)

    }

    private fun checkForMoveEvent(event: ScalingMotionEvent) {

        val area = currentCropArea

        if(event.isMove() && area != CropArea.NONE) {
            val lastEvent = this.lastEvent
            if(lastEvent != null) {
                cropRectWith(area, lastEvent, event)
            }
            this.lastEvent = event
        }

    }


    private fun ScalingMotionEvent.isDown(): Boolean {
        return original.action == MotionEvent.ACTION_DOWN && original.pointerCount == 1
    }

    private fun ScalingMotionEvent.isUp(): Boolean {
        return original.action == MotionEvent.ACTION_UP && original.pointerCount == 1
    }

    private fun ScalingMotionEvent.isMove(): Boolean {
        return original.action == MotionEvent.ACTION_MOVE && original.pointerCount == 1
    }


    private fun calculateCurrentCropArea(event: ScalingMotionEvent): CropArea {

        val inside = touchingInside(event)
        val left = touchingLeftHitBox(event)
        val right = touchingRightHitBox(event)
        val top = touchingTopHitBox(event)
        val bottom = touchingBottomHitBox(event)

        return if(top && left) {
            CropArea.TOP_LEFT
        } else if(top && right) {
            CropArea.TOP_RIGHT
        } else if(bottom && left) {
            CropArea.BOTTOM_LEFT
        } else if(bottom && right) {
            CropArea.BOTTOM_RIGHT
        } else if(top) {
            CropArea.TOP
        } else if(left) {
            CropArea.LEFT
        } else if(bottom) {
            CropArea.BOTTOM
        } else if(right) {
            CropArea.RIGHT
        } else if(inside) {
            CropArea.INSIDE
        } else {
            CropArea.NONE
        }

    }


    private fun touchingInside(event: ScalingMotionEvent): Boolean {
        return croppingRect.contains(event.mappedX, event.mappedY)
    }

    private fun touchingLeftHitBox(event: ScalingMotionEvent): Boolean {
        val hitBox = hitBox()
        bufferRect.top = croppingRect.top - hitBox
        bufferRect.bottom = croppingRect.bottom + hitBox
        bufferRect.left = croppingRect.left - hitBox
        bufferRect.right = croppingRect.left + hitBox
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun touchingRightHitBox(event: ScalingMotionEvent): Boolean {
        val hitBox = hitBox()
        bufferRect.top = croppingRect.top - hitBox
        bufferRect.bottom = croppingRect.bottom + hitBox
        bufferRect.left = croppingRect.right - hitBox
        bufferRect.right = croppingRect.right + hitBox
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun touchingTopHitBox(event: ScalingMotionEvent): Boolean {
        val hitBox = hitBox()
        bufferRect.top = croppingRect.top - hitBox
        bufferRect.bottom = croppingRect.top + hitBox
        bufferRect.left = croppingRect.left - hitBox
        bufferRect.right = croppingRect.right + hitBox
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun touchingBottomHitBox(event: ScalingMotionEvent): Boolean {
        val hitBox = hitBox()
        bufferRect.top = croppingRect.bottom - hitBox
        bufferRect.bottom = croppingRect.bottom + hitBox
        bufferRect.left = croppingRect.left - hitBox
        bufferRect.right = croppingRect.right + hitBox
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun hitBox(): Float {
        return getCroppingRectRadius()*CropModelCrop.HITBOX_FACTOR
    }

}
