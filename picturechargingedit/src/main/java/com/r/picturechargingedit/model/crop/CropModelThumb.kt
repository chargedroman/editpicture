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
        bottom += deltaHeight
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

        if(area == CropArea.INSIDE) {

        }

        println("CHAR: cr=${getCroppingRect()}")

    }

    private fun RectF.setLeft(value: Float) {
        left = value.coerceAtLeast(originalBoundsRect.left).coerceAtMost(right - minWidth)
    }

    private fun RectF.setRight(value: Float) {
        right = value.coerceAtLeast(left + minWidth).coerceAtMost(originalBoundsRect.right)
    }

    private fun RectF.setTop(value: Float) {
        top = value.coerceAtLeast(0f).coerceAtMost(bottom - minWidth)
    }

    private fun RectF.setBottom(value: Float) {
        bottom = value.coerceAtLeast(top + minWidth).coerceAtMost(originalBoundsRect.bottom)
    }


}
