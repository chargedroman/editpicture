package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.copyInto
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class CropModelThumb(pictureModel: Picture) : BaseCrop(pictureModel) {


    private val lastCropRect: RectF = RectF().apply { setEmpty() }
    private val minWidth = 400f


    override fun canDrawForMode(mode: EditPictureMode): Boolean = mode == EditPictureMode.THUMBNAIL


    override fun clear() {
        super.clear()
        lastCropRect.setEmpty()
    }


    override fun onBoundsUpdated() {
        if(getCroppingRect().isEmpty && !originalBoundsRect.isEmpty) {
            getCroppingRect().putInsideWithAspectRatio(originalBoundsRect)
            getCroppingRect().copyInto(lastCropRect)
        }
    }


    override fun cropRectWith(
        area: CropArea,
        lastEvent: ScalingMotionEvent,
        currentEvent: ScalingMotionEvent
    ) {

        val dx = currentEvent.mappedX - lastEvent.mappedX
        val dy = currentEvent.mappedY - lastEvent.mappedY

        lastCropRect.addIfPossible(area, dx, dy)
        lastCropRect.copyInto(getCroppingRect())
    }


    private fun RectF.putInsideWithAspectRatio(originalBounds: RectF) {
        originalBounds.copyInto(this)
        val deltaHeight = width() * getAspectRatio() - height()
        fixBottom(deltaHeight)
    }


    private fun RectF.addIfPossible(area: CropArea, dx: Float, dy: Float) {

        if(area == CropArea.LEFT || area == CropArea.BOTTOM_LEFT || area == CropArea.TOP_LEFT) {
            setLeft(left + dx)
            val deltaHeight = width() * getAspectRatio() - height()
            bottom += deltaHeight
        }

        if(area == CropArea.RIGHT || area == CropArea.BOTTOM_RIGHT || area == CropArea.TOP_RIGHT) {
            setRight(right + dx)
            val deltaHeight = width() * getAspectRatio() - height()
            bottom += deltaHeight
        }

        if(area == CropArea.TOP) {
            setRight(right - dy)
            val deltaHeight = width() * getAspectRatio() - height()
            bottom += deltaHeight
        }

        if(area == CropArea.BOTTOM) {
            setRight(right + dy)
            val deltaHeight = width() * getAspectRatio() - height()
            bottom += deltaHeight
        }

        addIfInside(area, dx, dy)

    }


    private fun RectF.addIfInside(area: CropArea, dx: Float, dy: Float) {

        if(area == CropArea.INSIDE) {

            if(right + dx <= originalBoundsRect.right && left + dx >= originalBoundsRect.left) {

                left += dx
                right += dx

            } else {

                val smallDX = if(dx > 0)
                    right - originalBoundsRect.right
                else
                    left - originalBoundsRect.left

                left -= smallDX
                right -= smallDX

            }

            if(bottom + dy <= originalBoundsRect.bottom && top + dy >= originalBoundsRect.top) {

                top += dy
                bottom += dy

            } else {

                val smallDY = if(dy > 0)
                    bottom - originalBoundsRect.bottom
                else
                    top - originalBoundsRect.top

                top -= smallDY
                bottom -= smallDY

            }

        }

    }


    private fun RectF.setLeft(value: Float) {
        left = value.coerceAtLeast(originalBoundsRect.left).coerceAtMost(right - minWidth)
    }

    private fun RectF.setRight(value: Float) {
        right = value.coerceAtLeast(left + minWidth).coerceAtMost(originalBoundsRect.right)
    }

    private fun RectF.fixBottom(deltaHeight: Float) {
        bottom = (bottom + deltaHeight).coerceAtLeast(top + minWidth).coerceAtMost(originalBoundsRect.bottom)
        val aspectHeight = height() / getAspectRatio()
        right = aspectHeight
    }


}
