package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val bufferRect: RectF = RectF().apply { setEmpty() }
    private var hasChanges = MutableLiveData(false)


    override fun hasChanges(): LiveData<Boolean> = hasChanges

    override fun canDrawForMode(mode: EditPictureMode): Boolean = mode == EditPictureMode.THUMBNAIL


    override fun clear() {
        super.clear()
        lastCropRect.setEmpty()
    }


    override fun onBoundsUpdated() {
        if (getCroppingRect().isEmpty && !originalBoundsRect.isEmpty) {
            getCroppingRect().putInsideWithAspectRatio(originalBoundsRect)
            getCroppingRect().copyInto(lastCropRect)
            hasChanges.postValue(false)
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

        hasChanges.postValue(true)
    }


    private fun RectF.putInsideWithAspectRatio(originalBounds: RectF) {
        originalBounds.copyInto(this)

        val radius = getCroppingRectRadius()
        val newWidth = (right - radius) - (left + radius)
        if(newWidth >= mappedMinWidth) {
            left += radius
            right -= radius
        }

        val deltaHeight = width() * getAspectRatio() - height()
        fixBottom(deltaHeight)

        val centerHeight = originalBounds.centerY() - height()
        if(bottom + centerHeight <= originalBounds.bottom) {
            top += centerHeight
            bottom += centerHeight
        }
    }


    private fun RectF.addIfPossible(area: CropArea, dx: Float, dy: Float) {
        when(area) {
            CropArea.NONE -> Unit
            CropArea.TOP_LEFT -> scaleLeft(dx)
            CropArea.BOTTOM_LEFT -> scaleLeft(dx)
            CropArea.TOP_RIGHT -> scaleRight(dx)
            CropArea.BOTTOM_RIGHT -> scaleRight(dx)
            else -> move(dx, dy)
        }
    }

    private fun RectF.scaleLeft(dx: Float) {
        copyInto(bufferRect)

        setLeft(left + dx)
        val deltaHeight = width() * getAspectRatio() - height()
        bottom += deltaHeight

        if(bottom >= originalBoundsRect.bottom) {
            bufferRect.copyInto(this)
        }
    }

    private fun RectF.scaleRight(dx: Float) {
        copyInto(bufferRect)

        setRight(right + dx)
        val deltaHeight = width() * getAspectRatio() - height()
        bottom += deltaHeight

        if(bottom >= originalBoundsRect.bottom) {
            bufferRect.copyInto(this)
        }
    }


    private fun RectF.move(dx: Float, dy: Float) {

        if (right + dx <= originalBoundsRect.right && left + dx >= originalBoundsRect.left) {

            left += dx
            right += dx

        } else {

            val smallDX = if (dx > 0)
                right - originalBoundsRect.right
            else
                left - originalBoundsRect.left

            left -= smallDX
            right -= smallDX

        }

        if (bottom + dy <= originalBoundsRect.bottom && top + dy >= originalBoundsRect.top) {

            top += dy
            bottom += dy

        } else {

            val smallDY = if (dy > 0)
                bottom - originalBoundsRect.bottom
            else
                top - originalBoundsRect.top

            top -= smallDY
            bottom -= smallDY

        }

    }


    private fun RectF.setLeft(value: Float) {
        left = value.coerceAtLeast(originalBoundsRect.left).coerceAtMost(right - mappedMinWidth)
    }

    private fun RectF.setRight(value: Float) {
        right = value.coerceAtLeast(left + mappedMinWidth).coerceAtMost(originalBoundsRect.right)
    }

    private fun RectF.fixBottom(deltaHeight: Float) {
        bottom = (bottom + deltaHeight).coerceAtLeast(top + mappedMinWidth * getAspectRatio())
            .coerceAtMost(originalBoundsRect.bottom)
        val aspectHeight = height() / getAspectRatio()
        right = left + aspectHeight
    }


}
