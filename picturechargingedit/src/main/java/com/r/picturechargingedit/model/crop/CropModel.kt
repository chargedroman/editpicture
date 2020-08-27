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

class CropModel(private val pictureModel: Picture): Crop {

    companion object {
        private const val HITBOX_FACTOR = 6f
        private const val MINSIZE_FACTOR = 24f
    }


    private enum class CropArea(val add: (RectF, Float, Float) -> Unit) {
        NONE({ _, _, _ -> }),

        LEFT({ rect, dx, _ -> rect.left += dx }),
        RIGHT({ rect, dx, _ -> rect.right += dx }),
        TOP({ rect, _, dy -> rect.top += dy }),
        BOTTOM({ rect, _, dy -> rect.bottom += dy }),

        TOP_LEFT({ rect, dx, dy -> rect.top += dy; rect.left += dx }),
        TOP_RIGHT({ rect, dx, dy -> rect.top += dy; rect.right += dx }),
        BOTTOM_LEFT({ rect, dx, dy -> rect.bottom += dy; rect.left += dx }),
        BOTTOM_RIGHT({ rect, dx, dy -> rect.bottom += dy; rect.right += dx });
    }


    private var currentCropArea: CropArea = CropArea.NONE
    private val originalBoundsRect: RectF = RectF()
    private val deltaRect: RectF = RectF()
    private val croppingRect: RectF = RectF()
    private val bufferRect: RectF = RectF()

    private var lastEvent: ScalingMotionEvent? = null
    private var croppingRectRadius: Float = 1f

    private var canDrawRect: Boolean = false


    override fun canDrawCrop(): Boolean = canDrawRect && !croppingRect.isZero()
    override fun getCroppingRect(): RectF = croppingRect
    override fun getCroppingRectRadius(): Float = croppingRectRadius


    override fun clear() {
        originalBoundsRect.set(0f, 0f, 0f, 0f)
        originalBoundsRect.copyInto(croppingRect)
        originalBoundsRect.copyInto(deltaRect)
        originalBoundsRect.copyInto(bufferRect)
        currentCropArea = CropArea.NONE
        lastEvent = null
    }


    override fun setMode(mode: EditPictureMode) {
        canDrawRect = mode.isCropping()
        updateBounds()
    }


    override fun onTouchEvent(event: ScalingMotionEvent) {

        if(!canDrawRect) {
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
        left = left.coerceAtLeast(rectF.left).coerceAtMost(rectF.right)
        right = right.coerceAtLeast(rectF.left).coerceAtMost(rectF.right)
        top = top.coerceAtLeast(rectF.top).coerceAtMost(rectF.bottom)
        bottom = bottom.coerceAtLeast(rectF.top).coerceAtMost(rectF.bottom)

        val minSize = croppingRectRadius*MINSIZE_FACTOR
        top = top.coerceAtMost(bottom - minSize).coerceAtLeast(rectF.top)
        bottom = bottom.coerceAtLeast(top + minSize).coerceAtMost(rectF.bottom)
        left = left.coerceAtMost(right - minSize).coerceAtLeast(rectF.left)
        right = right.coerceAtLeast(left + minSize).coerceAtMost(rectF.right)
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
        } else {
            CropArea.NONE
        }
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
        return croppingRectRadius*HITBOX_FACTOR
    }

}
