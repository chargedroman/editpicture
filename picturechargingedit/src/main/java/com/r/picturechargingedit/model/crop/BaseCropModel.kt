package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import android.view.MotionEvent
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.add
import com.r.picturechargingedit.arch.copyInto
import com.r.picturechargingedit.arch.isZero
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

abstract class BaseCropModel(private val pictureModel: Picture): Crop {

    companion object {
        const val HITBOX_FACTOR = 4f
        const val MINSIZE_FACTOR = 24f
    }


    protected val originalBoundsRect: RectF = RectF()

    private val deltaRect: RectF = RectF()
    private val croppingRect: RectF = RectF()
    private val bufferRect: RectF = RectF()

    private var currentCropArea: CropArea = CropArea.NONE
    private var lastEvent: ScalingMotionEvent? = null
    private var croppingRectRadius: Float = 1f
    private var currentMode: EditPictureMode = EditPictureMode.NONE


    override fun getCroppingRect(): RectF = croppingRect
    override fun getCroppingRectRadius(): Float = croppingRectRadius


    abstract fun getDrawMode(): EditPictureMode
    abstract fun getMinWidthCroppingRectLimit(): Float
    abstract fun getMinHeightCroppingRectLimit(): Float
    abstract fun limitCroppingRectToOriginalBounds(): Boolean
    abstract fun calculateCurrentCropArea(event: ScalingMotionEvent): CropArea


    override fun canDraw(): Boolean {
        updateBounds()
        return currentMode == getDrawMode() && !getCroppingRect().isZero()
    }


    override fun clear() {
        originalBoundsRect.set(0f, 0f, 0f, 0f)
        originalBoundsRect.copyInto(croppingRect)
        originalBoundsRect.copyInto(deltaRect)
        originalBoundsRect.copyInto(bufferRect)
        currentCropArea = CropArea.NONE
        lastEvent = null
    }


    override fun setMode(mode: EditPictureMode) {
        this.currentMode = mode
        updateBounds()
    }


    override fun onTouchEvent(event: ScalingMotionEvent) {

        if(currentMode != getDrawMode()) {
            return
        }

        updateBounds()
        actDependingOnEventAction(event)
    }

    private fun updateBounds() {
        if(croppingRect.isZero()) {
            originalBoundsRect.copyInto(croppingRect)
        }

        croppingRectRadius = pictureModel.getBitmapMargin()

        pictureModel.getBitmapBounds().copyInto(bufferRect)
        pictureModel.getMatrix().mapRect(bufferRect)
        bufferRect.copyInto(originalBoundsRect)

        if(limitCroppingRectToOriginalBounds()) {
            croppingRect.limitBoundsTo(originalBoundsRect)
        }
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

        if(event.isMove() && currentCropArea != CropArea.NONE) {
            cropRectWith(event)
            this.lastEvent = event
        }
    }


    private fun cropRectWith(event: ScalingMotionEvent) {
        val lastEvent = lastEvent
        val area = currentCropArea

        if(lastEvent == null) {
            return
        }

        val dx = event.mappedX - lastEvent.mappedX
        val dy = event.mappedY - lastEvent.mappedY

        area.add(deltaRect, dx, dy)

        originalBoundsRect.copyInto(bufferRect)
        bufferRect.add(deltaRect)
        bufferRect.copyInto(croppingRect)
        croppingRect.limitBoundsTo(originalBoundsRect)
    }


    private fun RectF.limitBoundsTo(rectF: RectF) {
        val minHeight = getMinHeightCroppingRectLimit()
        val minWidth = getMinWidthCroppingRectLimit()

        top = top.coerceAtLeast(rectF.top)
        bottom = bottom.coerceAtLeast(top + minHeight).coerceAtMost(rectF.bottom)
        top = top.coerceAtMost(bottom - minHeight)

        left = left.coerceAtLeast(rectF.left)
        right = right.coerceAtLeast(left + minWidth).coerceAtMost(rectF.right)
        left = left.coerceAtMost(right - minWidth)

        if(limitCroppingRectToOriginalBounds()) {
            bottom = bottom.coerceAtMost(top + minHeight)
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



    protected fun touchingInside(event: ScalingMotionEvent): Boolean {
        return croppingRect.contains(event.mappedX, event.mappedY)
    }

    protected fun touchingLeftHitBox(event: ScalingMotionEvent): Boolean {
        val hitBox = hitBox()
        bufferRect.top = croppingRect.top - hitBox
        bufferRect.bottom = croppingRect.bottom + hitBox
        bufferRect.left = croppingRect.left - hitBox
        bufferRect.right = croppingRect.left + hitBox
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    protected fun touchingRightHitBox(event: ScalingMotionEvent): Boolean {
        val hitBox = hitBox()
        bufferRect.top = croppingRect.top - hitBox
        bufferRect.bottom = croppingRect.bottom + hitBox
        bufferRect.left = croppingRect.right - hitBox
        bufferRect.right = croppingRect.right + hitBox
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    protected fun touchingTopHitBox(event: ScalingMotionEvent): Boolean {
        val hitBox = hitBox()
        bufferRect.top = croppingRect.top - hitBox
        bufferRect.bottom = croppingRect.top + hitBox
        bufferRect.left = croppingRect.left - hitBox
        bufferRect.right = croppingRect.right + hitBox
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    protected fun touchingBottomHitBox(event: ScalingMotionEvent): Boolean {
        val hitBox = hitBox()
        bufferRect.top = croppingRect.bottom - hitBox
        bufferRect.bottom = croppingRect.bottom + hitBox
        bufferRect.left = croppingRect.left - hitBox
        bufferRect.right = croppingRect.right + hitBox
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun hitBox(): Float {
        return croppingRectRadius*HITBOX_FACTOR
    }


}
