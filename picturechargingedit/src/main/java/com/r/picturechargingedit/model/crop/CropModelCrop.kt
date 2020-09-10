package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.add
import com.r.picturechargingedit.arch.copyInto
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class CropModelCrop(pictureModel: Picture) : BaseCrop(pictureModel) {


    companion object {
        const val HITBOX_FACTOR = 4f
    }


    private val deltaRect: RectF = RectF()
    private val bufferRect: RectF = RectF()
    private var hasChanges = MutableLiveData(false)


    override fun hasChanges(): LiveData<Boolean> = hasChanges

    override fun canDrawForMode(mode: EditPictureMode): Boolean = mode == EditPictureMode.CROP


    override fun onBoundsUpdated() {
        if(getCroppingRect().isEmpty) {
            originalBoundsRect.copyInto(getCroppingRect())
            hasChanges.postValue(false)
        }
    }


    override fun clear() {
        super.clear()
        deltaRect.setEmpty()
        bufferRect.setEmpty()
    }


    override fun cropRectWith(
        area: CropArea,
        lastEvent: ScalingMotionEvent,
        currentEvent: ScalingMotionEvent
    ) {

        val dx = currentEvent.mappedX - lastEvent.mappedX
        val dy = currentEvent.mappedY - lastEvent.mappedY

        area.add(deltaRect, dx, dy)

        originalBoundsRect.copyInto(bufferRect)
        bufferRect.add(deltaRect)
        bufferRect.copyInto(getCroppingRect())
        getCroppingRect().limitBoundsTo(originalBoundsRect)

        hasChanges.postValue(true)
    }


    private fun RectF.limitBoundsTo(rectF: RectF) {
        top = top.coerceAtLeast(rectF.top)
        bottom = bottom.coerceAtLeast(top + mappedMinWidth).coerceAtMost(rectF.bottom)
        top = top.coerceAtMost(bottom - mappedMinWidth)

        left = left.coerceAtLeast(rectF.left)
        right = right.coerceAtLeast(left + mappedMinWidth).coerceAtMost(rectF.right)
        left = left.coerceAtMost(right - mappedMinWidth)
    }


}
