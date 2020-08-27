package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import android.view.MotionEvent
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.add
import com.r.picturechargingedit.arch.copyInto
import com.r.picturechargingedit.arch.setZero
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

class CropModel(private val pictureModel: Picture): Crop {


    private enum class CropArea(val add: (RectF, Float, Float) -> Unit) {
        NONE({ _, _, _ -> }),

        LEFT({ rect, dx, _ -> rect.left += dx }),
        RIGHT({ rect, dx, _ -> rect.right += dx }),
        TOP({ rect, _, dy -> rect.top += dy }),
        BOTTOM({ rect, _, dy -> rect.bottom += dy }),

        TOPLEFT({ rect, dx, dy -> rect.top += dy; rect.left += dx }),
        TOPRIGHT({ rect, dx, dy -> rect.top += dy; rect.right += dx }),
        BOTTOMLEFT({ rect, dx, dy -> rect.bottom += dy; rect.left += dx }),
        BOTTOMRIGHT({ rect, dx, dy -> rect.bottom += dy; rect.right += dx });
    }


    private var currentCropArea: CropArea = CropArea.NONE
    private val originalBoundsRect: RectF = RectF()
    private val deltaRect: RectF = RectF()
    private val croppingRect: RectF = RectF()
    private val bufferRect: RectF = RectF()

    private var lastEvent: ScalingMotionEvent? = null
    private var croppingRectRadius: Float = 1f

    private var canShowRect: Boolean = false


    override fun getCroppingRect(): RectF = croppingRect
    override fun getCroppingRectRadius(): Float = croppingRectRadius




    override fun setMode(mode: EditPictureMode) {
        canShowRect = mode.isCropping()
    }


    override fun onTouchEvent(event: ScalingMotionEvent) {

        if(!canShowRect) {
            croppingRect.setZero()
            return
        }

        pictureModel.getBitmapBoundsMapped().copyInto(originalBoundsRect)

        if(event.isDown()) {
            croppingRectRadius = event.mappedMargin
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
        left = left.coerceAtLeast(rectF.left).coerceAtMost(rectF.right)
        right = right.coerceAtLeast(rectF.left).coerceAtMost(rectF.right)
        top = top.coerceAtLeast(rectF.top).coerceAtMost(rectF.bottom)
        bottom = bottom.coerceAtLeast(rectF.top).coerceAtMost(rectF.bottom)
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

        val left = touchingLeftHitBox(event)
        val right = touchingRightHitBox(event)
        val top = touchingTopHitBox(event)
        val bottom = touchingBottomHitBox(event)

        return if(top && left) {
            CropArea.TOPLEFT
        } else if(top && right) {
            CropArea.TOPRIGHT
        } else if(bottom && left) {
            CropArea.BOTTOMLEFT
        } else if(bottom && right) {
            CropArea.BOTTOMRIGHT
        } else if(top) {
            CropArea.TOP
        } else if(left) {
            CropArea.LEFT
        } else if(bottom) {
            CropArea.BOTTOM
        } else if(right) {
            CropArea.RIGHT
        } else {
            CropArea.NONE
        }
    }

    private fun touchingLeftHitBox(event: ScalingMotionEvent): Boolean {
        bufferRect.top = croppingRect.top - event.hitBox()
        bufferRect.bottom = croppingRect.bottom + event.hitBox()
        bufferRect.left = croppingRect.left - event.hitBox()
        bufferRect.right = croppingRect.left + event.hitBox()
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun touchingRightHitBox(event: ScalingMotionEvent): Boolean {
        bufferRect.top = croppingRect.top - event.hitBox()
        bufferRect.bottom = croppingRect.bottom + event.hitBox()
        bufferRect.left = croppingRect.right - event.hitBox()
        bufferRect.right = croppingRect.right + event.hitBox()
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun touchingTopHitBox(event: ScalingMotionEvent): Boolean {
        bufferRect.top = croppingRect.top - event.hitBox()
        bufferRect.bottom = croppingRect.top + event.hitBox()
        bufferRect.left = croppingRect.left - event.hitBox()
        bufferRect.right = croppingRect.right + event.hitBox()
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun touchingBottomHitBox(event: ScalingMotionEvent): Boolean {
        bufferRect.top = croppingRect.bottom - event.hitBox()
        bufferRect.bottom = croppingRect.bottom + event.hitBox()
        bufferRect.left = croppingRect.left - event.hitBox()
        bufferRect.right = croppingRect.right + event.hitBox()
        return bufferRect.contains(event.mappedX, event.mappedY)
    }

    private fun ScalingMotionEvent.hitBox(): Float {
        return mappedMargin*2f
    }

}
