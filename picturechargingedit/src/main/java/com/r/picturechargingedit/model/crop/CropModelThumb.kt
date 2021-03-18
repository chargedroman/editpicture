package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.copyInto
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent
import kotlin.math.abs

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

open class CropModelThumb(pictureModel: Picture) : BaseCrop(pictureModel) {


    companion object {
        const val HITBOX_FACTOR = 4f
    }

    override val hitBoxFactor: Float get() = HITBOX_FACTOR


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
            updateInitialBoundsToBufferRect()
            getCroppingRect().putInsideWithAspectRatio(bufferRect)
            getCroppingRect().copyInto(lastCropRect)
            hasChanges.postValue(false)
        }
    }

    /**
     * updates [bufferRect] in such a way, that its a square and in the center of [originalBoundsRect]
     */
    private fun updateInitialBoundsToBufferRect() {
        val maxWidth = originalBoundsRect.width()
        val maxHeight = originalBoundsRect.height()
        val diff = abs(maxWidth - maxHeight)/2
        originalBoundsRect.copyInto(bufferRect)

        if(maxWidth < maxHeight) {
            bufferRect.bottom = (bufferRect.bottom - diff).coerceAtLeast(0f)
            bufferRect.top = (bufferRect.top + diff).coerceAtMost(bufferRect.bottom)
        } else {
            bufferRect.right = (bufferRect.right - diff).coerceAtLeast(0f)
            bufferRect.left = (bufferRect.left + diff).coerceAtMost(bufferRect.right)
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

        val deltaHeight = width() * getAspectRatio() - height()
        fixBottom(deltaHeight)

        val centerHeight = (originalBounds.centerY() - height()).coerceAtLeast(0f)
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
